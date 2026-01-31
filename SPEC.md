# DeliveryTracker Android - Προδιαγραφές Εφαρμογής v1.0

## 📋 Επισκόπηση

| Πεδίο | Τιμή |
|-------|------|
| **Όνομα** | DeliveryTracker |
| **Πλατφόρμα** | Android (min SDK 26) |
| **Package** | com.deliverytracker.app |
| **Backend** | Firebase (Spark Plan) |
| **Γλώσσα UI** | Ελληνικά + Αγγλικά |
| **Τύπος** | Online Single-user Mobile App |

**Σκοπός:** Εφαρμογή για freelancer διανομείς (efood, Wolt κλπ.) για καταχώρηση και παρακολούθηση βαρδιών, εσόδων, εξόδων και στατιστικών.

---

## 🔐 Ενότητα 1: Αυθεντικοποίηση

### 1.1 Εγγραφή
| Πεδίο | Τύπος | Validation |
|-------|-------|------------|
| Email | String | Valid email format |
| Κωδικός | String | Min 6 χαρακτήρες |
| Όνομα Χρήστη | String | 3-50 χαρακτήρες |

### 1.2 Σύνδεση
- Firebase Authentication (Email/Password)
- Remember me option
- Password reset via email

### 1.3 PIN Security
| Ρύθμιση | Περιγραφή |
|---------|-----------|
| Ορισμός PIN | 4-6 ψηφία |
| Χρήση | Επιβεβαίωση διαγραφών |
| Lockout | 3 αποτυχίες → 30sec cooldown |

---

## 🗂️ Ενότητα 2: Βάρδιες

### 2.1 Πεδία Βάρδιας
| Πεδίο | Τύπος | Validation |
|-------|-------|------------|
| Ημερομηνία | Date | Όχι μελλοντικές |
| Έσοδα Πλατφόρμας | Decimal | ≥0.01€ |
| Φιλοδωρήματα | Decimal | ≥0€ |
| Μπόνους | Decimal | ≥0€ |
| Παραγγελίες | Integer | ≥1 |
| Χιλιόμετρα | Decimal | ≥0.1 |
| Ώρες | Integer | 0-23 |
| Λεπτά | Integer | 0-59 |
| Σημειώσεις | String | Optional, max 500 |

### 2.2 Validation Rules
- ❌ Μελλοντικές ημερομηνίες
- ❌ Αρνητικοί αριθμοί
- ❌ Διάρκεια > 24 ώρες
- ✅ Πολλές βάρδιες την ίδια ημέρα

---

## 💰 Ενότητα 3: Έξοδα

### 3.1 Πεδία
| Πεδίο | Τύπος | Validation |
|-------|-------|------------|
| Ημερομηνία | Date | Όχι μελλοντικές |
| Κατηγορία | Enum | Από λίστα |
| Ποσό | Decimal | ≥0.01€ |
| Μέθοδος Πληρωμής | Enum | Μετρητά/Κάρτα |
| Σημειώσεις | String | Optional, max 500 |

### 3.2 Κατηγορίες Εξόδων
| ID | Κατηγορία | Tax Deductible |
|----|-----------|----------------|
| 1 | Καύσιμα | ✅ |
| 2 | Συντήρηση / Service | ✅ |
| 3 | Ασφάλεια | ✅ |
| 4 | Κινητό / Data | ✅ (partial) |
| 5 | Εξοπλισμός | ✅ |
| 6 | ΚΤΕΟ | ✅ |
| 7 | Τέλη Κυκλοφορίας | ✅ |
| 8 | Πρόστιμα | ❌ |
| 9 | Άλλο | Depends |

---

## 📊 Ενότητα 4: Dashboard & Στατιστικά

### 4.1 Metrics
| Metric | Υπολογισμός |
|--------|-------------|
| Καθαρό Εισόδημα | Έσοδα + Tips + Bonus |
| Μέσος όρος/ώρα | Καθαρό ÷ Ώρες |
| Με ΦΠΑ | (Έσοδα + Bonus) × 1.24 + Tips |
| Καθαρό Κέρδος | Καθαρό - Έξοδα |

### 4.2 Progress Bars
| Χρώμα | Ποσοστό |
|-------|---------|
| 🟢 Πράσινο | ≥100% |
| 🟠 Πορτοκαλί | 50-99% |
| 🔴 Κόκκινο | <50% |

### 4.3 Period Filters
- Σήμερα
- Αυτή την εβδομάδα
- Αυτόν τον μήνα
- Αυτό το έτος
- Custom range

---

## ⚙️ Ενότητα 5: Ρυθμίσεις

### 5.1 Γενικές
| Ρύθμιση | Default |
|---------|---------|
| Θέμα | System |
| Γλώσσα | Auto-detect |

### 5.2 Φορολογικές
| Ρύθμιση | Default |
|---------|---------|
| ΦΠΑ (%) | 24% |
| ΕΦΚΑ (€/μήνα) | 254€ |

### 5.3 Στόχοι
- Ημερήσιος στόχος (€)
- Μηνιαίος στόχος (€)
- Ετήσιος στόχος (€)

---

## 🗑️ Ενότητα 6: Κάδος Ανακύκλωσης

| Λειτουργία | Περιγραφή |
|------------|-----------|
| Soft Delete | isDeleted = true |
| Επαναφορά | Restore από κάδο |
| Auto-cleanup | Οριστική διαγραφή μετά 30 ημέρες |

---

## 🎨 Ενότητα 7: UI/UX

### Navigation
- Bottom Navigation: Dashboard, Βάρδιες, Έξοδα, Ρυθμίσεις
- FAB για quick add
- Pull-to-refresh
- Swipe-to-delete

### Theme Colors
| Purpose | Light | Dark |
|---------|-------|------|
| Primary | #1976D2 | #90CAF9 |
| Success | #4CAF50 | #81C784 |
| Warning | #FF9800 | #FFB74D |
| Error | #F44336 | #E57373 |

---

## 🔥 Firebase Collections

```
/users/{userId}
├── email, username, role
├── pinHash, failedPinAttempts
└── /settings/{userId}

/shifts/{shiftId}
├── userId, date
├── platformEarnings, tips, bonus
├── ordersCount, kilometers
├── durationHours, durationMinutes
└── isDeleted, deletedAt

/expenses/{expenseId}
├── userId, date, category
├── amount, paymentMethod
└── isDeleted, deletedAt
```

---

## 📅 Changelog

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2026-01-31 | Initial Android version based on WinUI desktop app |
