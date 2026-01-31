# 📱 DeliveryTracker Android

Εφαρμογή διαχείρισης για επαγγελματίες διανομείς (efood, Wolt, BOX).

## 🚀 Χαρακτηριστικά
- ✅ Καταγραφή βαρδιών και εσόδων
- ✅ Παρακολούθηση εξόδων
- ✅ Dashboard με στατιστικά
- ✅ Στόχοι και πρόοδος
- ✅ Light/Dark theme
- ✅ Ελληνικά + Αγγλικά

## 🛠️ Τεχνολογίες
- Kotlin + Jetpack Compose
- Firebase (Auth + Firestore)
- Material Design 3

## 📦 Setup

### 1. Firebase Configuration
1. Πήγαινε στο [Firebase Console](https://console.firebase.google.com)
2. Δημιούργησε νέο project "DeliveryTracker"
3. Πρόσθεσε Android app με package `com.deliverytracker.app`
4. Κατέβασε το `google-services.json`
5. Τοποθέτησέ το στο `/app/`

### 2. Build
```bash
# Άνοιξε με Android Studio
# ή από terminal:
./gradlew assembleDebug
```

### 3. Run
```bash
./gradlew installDebug
```

## 📖 Documentation
- [GEMINI.md](GEMINI.md) - Οδηγίες για το AI
- [SPEC.md](SPEC.md) - Αναλυτικές προδιαγραφές

## 📄 License
Private - All rights reserved
