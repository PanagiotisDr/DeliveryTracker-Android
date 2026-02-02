package com.deliverytracker.app.data.repository

import com.deliverytracker.app.domain.model.Result
import com.deliverytracker.app.domain.model.User
import com.deliverytracker.app.domain.model.UserRole
import com.deliverytracker.app.domain.repository.AuthRepository
import com.deliverytracker.app.domain.security.PinHasher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Υλοποίηση του AuthRepository με Firebase.
 * Χρησιμοποιεί Firebase Auth για authentication και Firestore για user data.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    
    // Collection reference για τους χρήστες
    private val usersCollection = firestore.collection("users")
    
    override val currentUserState: Flow<Result<User?>> = callbackFlow {
        val authListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                trySend(Result.Loading)
                usersCollection.document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val user = doc.toUser()
                        if (user == null) {
                            trySend(Result.Error("Αποτυχία φόρτωσης χρήστη"))
                        } else {
                            trySend(Result.Success(user))
                        }
                    }
                    .addOnFailureListener { error ->
                        trySend(Result.Error("Σφάλμα φόρτωσης χρήστη", error))
                    }
            } else {
                trySend(Result.Success(null))
            }
        }
        
        firebaseAuth.addAuthStateListener(authListener)
        
        awaitClose {
            firebaseAuth.removeAuthStateListener(authListener)
        }
    }

    override val currentUser: Flow<User?> =
        currentUserState.map { result -> result.getOrNull() }
    
    override val isLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null
    
    override fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        // Επιστρέφουμε βασικό User object με το uid
        // Τα πλήρη δεδομένα θα φορτωθούν μέσω του Flow
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            username = ""
        )
    }
    
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            // Σύνδεση με Firebase Auth
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.Error("Αποτυχία σύνδεσης")
            
            // Φόρτωσε τα user data από Firestore
            val userDoc = usersCollection.document(firebaseUser.uid).get().await()
            val user = userDoc.toUser()
                ?: return Result.Error("Δεν βρέθηκε ο χρήστης")
            
            // Ενημέρωσε το lastLoginAt
            usersCollection.document(firebaseUser.uid)
                .update("lastLoginAt", System.currentTimeMillis())
                .await()
            
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(mapFirebaseError(e), e)
        }
    }
    
    override suspend fun register(
        email: String,
        password: String,
        username: String
    ): Result<User> {
        return try {
            // Δημιουργία λογαριασμού στο Firebase Auth
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.Error("Αποτυχία εγγραφής")
            
            // Έλεγχος αν είναι ο πρώτος χρήστης (admin)
            val usersSnapshot = usersCollection.limit(1).get().await()
            val isFirstUser = usersSnapshot.isEmpty
            
            // Δημιουργία user document στο Firestore
            val user = User(
                id = firebaseUser.uid,
                email = email,
                username = username,
                role = if (isFirstUser) UserRole.ADMIN else UserRole.USER,
                lastLoginAt = System.currentTimeMillis()
            )
            
            usersCollection.document(firebaseUser.uid)
                .set(user.toMap())
                .await()
            
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(mapFirebaseError(e), e)
        }
    }
    
    override suspend fun logout() {
        firebaseAuth.signOut()
    }
    
    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(mapFirebaseError(e), e)
        }
    }
    
    override suspend fun updatePin(userId: String, pin: String?): Result<Unit> {
        return try {
            val updates = if (pin.isNullOrBlank()) {
                mapOf(
                    "pinHash" to null,
                    "pinSalt" to null,
                    "failedPinAttempts" to 0,
                    "pinLockoutEnd" to null
                )
            } else {
                val hashResult = PinHasher.createHash(pin)
                mapOf(
                    "pinHash" to hashResult.hash,
                    "pinSalt" to hashResult.salt,
                    "failedPinAttempts" to 0,
                    "pinLockoutEnd" to null
                )
            }
            
            usersCollection.document(userId).update(updates).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Σφάλμα ενημέρωσης PIN", e)
        }
    }
    
    override suspend fun verifyPin(userId: String, pin: String): Result<Boolean> {
        return try {
            val doc = usersCollection.document(userId).get().await()
            val storedHash = doc.getString("pinHash")
            if (storedHash.isNullOrBlank()) {
                return Result.Success(false)
            }
            
            val storedSalt = doc.getString("pinSalt")
            val isValid = if (!storedSalt.isNullOrBlank()) {
                PinHasher.verify(pin, storedSalt, storedHash)
            } else {
                PinHasher.verifyLegacy(pin, storedHash)
            }
            
            if (isValid && storedSalt.isNullOrBlank()) {
                val upgraded = PinHasher.createHash(pin)
                usersCollection.document(userId).update(
                    mapOf(
                        "pinHash" to upgraded.hash,
                        "pinSalt" to upgraded.salt
                    )
                ).await()
            }
            
            Result.Success(isValid)
        } catch (e: Exception) {
            Result.Error("Σφάλμα επαλήθευσης PIN", e)
        }
    }
    
    override suspend fun recordFailedPinAttempt(userId: String): Result<Int> {
        return try {
            val doc = usersCollection.document(userId).get().await()
            val currentAttempts = doc.getLong("failedPinAttempts")?.toInt() ?: 0
            val newAttempts = currentAttempts + 1
            
            val updates = mutableMapOf<String, Any?>(
                "failedPinAttempts" to newAttempts
            )
            
            // Lockout μετά από 3 αποτυχίες για 30 δευτερόλεπτα
            if (newAttempts >= 3) {
                updates["pinLockoutEnd"] = System.currentTimeMillis() + 30_000
            }
            
            usersCollection.document(userId).update(updates).await()
            Result.Success(newAttempts)
        } catch (e: Exception) {
            Result.Error("Σφάλμα καταγραφής", e)
        }
    }
    
    override suspend fun resetPinLockout(userId: String): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .update(mapOf(
                    "failedPinAttempts" to 0,
                    "pinLockoutEnd" to null
                ))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Σφάλμα επαναφοράς", e)
        }
    }
    
    // ============ Helper Functions ============
    
    /**
     * Μετατροπή Firestore document σε User object.
     */
    private fun com.google.firebase.firestore.DocumentSnapshot.toUser(): User? {
        return try {
            User(
                id = id,
                email = getString("email") ?: "",
                username = getString("username") ?: "",
                role = getString("role")?.let { UserRole.valueOf(it) } ?: UserRole.USER,
                pinHash = getString("pinHash"),
                pinSalt = getString("pinSalt"),
                failedPinAttempts = getLong("failedPinAttempts")?.toInt() ?: 0,
                pinLockoutEnd = getLong("pinLockoutEnd"),
                lastLoginAt = getLong("lastLoginAt"),
                createdAt = getLong("createdAt") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Μετατροπή User σε Map για Firestore.
     */
    private fun User.toMap(): Map<String, Any?> = mapOf(
        "email" to email,
        "username" to username,
        "role" to role.name,
        "pinHash" to pinHash,
        "pinSalt" to pinSalt,
        "failedPinAttempts" to failedPinAttempts,
        "pinLockoutEnd" to pinLockoutEnd,
        "lastLoginAt" to lastLoginAt,
        "createdAt" to createdAt
    )
    
    /**
     * Μετατροπή Firebase errors σε user-friendly μηνύματα.
     */
    private fun mapFirebaseError(e: Exception): String {
        return when {
            e.message?.contains("INVALID_EMAIL") == true -> 
                "Μη έγκυρη διεύθυνση email"
            e.message?.contains("WRONG_PASSWORD") == true || 
            e.message?.contains("INVALID_LOGIN_CREDENTIALS") == true -> 
                "Λάθος email ή κωδικός"
            e.message?.contains("USER_NOT_FOUND") == true -> 
                "Δεν βρέθηκε χρήστης με αυτό το email"
            e.message?.contains("EMAIL_EXISTS") == true || 
            e.message?.contains("EMAIL_ALREADY_IN_USE") == true -> 
                "Το email χρησιμοποιείται ήδη"
            e.message?.contains("WEAK_PASSWORD") == true -> 
                "Ο κωδικός πρέπει να έχει τουλάχιστον 6 χαρακτήρες"
            e.message?.contains("NETWORK") == true -> 
                "Σφάλμα σύνδεσης. Ελέγξτε το internet."
            else -> "Σφάλμα: ${e.localizedMessage ?: "Άγνωστο σφάλμα"}"
        }
    }
}
