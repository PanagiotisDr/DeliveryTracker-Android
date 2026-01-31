# DeliveryTracker Android - Enterprise Edition

## 📋 Επισκόπηση Project
Το **DeliveryTracker** είναι μια premium Android εφαρμογή διαχείρισης για επαγγελματίες διανομείς (freelancers). Επιτρέπει την καταγραφή βαρδιών, εσόδων, εξόδων και την παρακολούθηση στατιστικών και οικονομικών στόχων.

### Τεχνικό Stack
| Component | Technology |
|-----------|------------|
| **Γλώσσα** | Kotlin 2.x |
| **UI** | Jetpack Compose + Material Design 3 |
| **Backend** | Firebase (Firestore + Auth) |
| **Αρχιτεκτονική** | Clean Architecture + MVVM |
| **DI** | Hilt |
| **Async** | Kotlin Coroutines + StateFlow |
| **Charts** | Vico |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | 35 (Android 15) |

---

## 🏗️ Δομή και Αρχιτεκτονική
Το project ακολουθεί αυστηρά το **Clean Architecture**:

### 1. 🔵 Domain Layer (`domain/`)
Ο πυρήνας της εφαρμογής. Models, UseCases, Repository Interfaces.
- **Δεν έχει εξαρτήσεις** από άλλα layers.
- **Βασικά Models:** `User`, `Shift`, `Expense`, `UserSettings`.

### 2. 🟠 Data Layer (`data/`)
Υλοποιεί τα repository interfaces και διαχειρίζεται Firebase.
- **Περιεχόμενα:** Repository implementations, DTOs, FirebaseService.
- **Εξαρτάται από:** Domain.

### 3. 🟣 Presentation Layer (`presentation/`)
Η διεπαφή χρήστη σε Jetpack Compose.
- **Περιεχόμενα:** Screens, ViewModels, Components, Theme.
- **Εξαρτάται από:** Domain.

---

## 🔥 Firebase Configuration

### Project Details
| Setting | Value |
|---------|-------|
| **Admin Email** | panagiotis.dr82@gmail.com |
| **Package Name** | com.deliverytracker.app |
| **Plan** | Spark (Free) |

### Services Used
- **Authentication:** Email/Password
- **Firestore:** Main database
- **Analytics:** Usage tracking

---

## 🇬🇷 Κανόνες Γλώσσας (ΑΥΣΤΗΡΟΙ)

| Τομέας | Γλώσσα | Παράδειγμα |
|--------|--------|------------|
| **Επικοινωνία μαζί σου** | Ελληνικά | "Θα διορθώσω το bug..." |
| **Σχόλια Κώδικα** | Ελληνικά | `// Υπολογισμός ΦΠΑ 24%` |
| **UI Strings** | Ελληνικά + Αγγλικά | `strings.xml` + `strings.xml (el)` |
| **Commit Messages** | Ελληνικά | `Προσθήκη οθόνης σύνδεσης` |

> **Εξαίρεση:** Ονόματα μεταβλητών, κλάσεων και αρχείων στα Αγγλικά.

---

## 🛠️ Build & Run

### Απαιτήσεις
- Android Studio Ladybug (2024.2+)
- JDK 17+
- Firebase account

### Εντολές
```bash
# Build
./gradlew assembleDebug

# Run tests
./gradlew test

# Install στη συσκευή
./gradlew installDebug
```

---

## 📝 Συμβάσεις Ανάπτυξης

1. **Hilt DI:** Constructor injection παντού
2. **Validation:** Custom validators στα UseCases
3. **Resources:** Όλα τα strings στο `res/values/strings.xml`
4. **Async:** Coroutines + StateFlow για state management
5. **MVVM:** Κάθε Screen έχει αντίστοιχο ViewModel

---

## 🚫 Zero Hardcoding Rule

| Τι | Πού πηγαίνει |
|----|--------------|
| **Texts/Strings** | `res/values/strings.xml` |
| **Colors** | `theme/Color.kt` |
| **Dimensions** | `theme/` ή constants |

---

## ⚠️ Σημαντική Σημείωση για το AI
**Ο χρήστης ΔΕΝ είναι προγραμματιστής.**
- Μην υποθέτεις. **ΡΩΤΑ** αν υπάρχει ασάφεια.
- Εξήγησε τις ενέργειές σου με απλά λόγια.
- Συμβουλέψου πάντα το `SPEC.md` πριν από κάθε υλοποίηση.
