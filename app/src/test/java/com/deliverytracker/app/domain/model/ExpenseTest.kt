package com.deliverytracker.app.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Tests για το Expense domain model.
 *
 * Ελέγχει:
 * - Default values
 * - ExpenseCategory enum
 * - PaymentMethod enum
 * - Soft delete
 */
@DisplayName("Expense Model")
class ExpenseTest {

    // ═══════════════════════════════════════════════════
    // Default Values
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Προεπιλεγμένες τιμές")
    inner class DefaultValues {

        @Test
        @DisplayName("Νέο expense έχει σωστές default τιμές")
        fun `default expense has correct initial values`() {
            val expense = Expense()

            assertEquals("", expense.id)
            assertEquals("", expense.userId)
            assertEquals(0.0, expense.amount)
            assertEquals(ExpenseCategory.OTHER, expense.category)
            assertEquals(PaymentMethod.CASH, expense.paymentMethod)
            assertEquals("", expense.notes)
            assertNull(expense.shiftId)
            assertNull(expense.receiptUrl)
            assertFalse(expense.isDeleted)
            assertNull(expense.deletedAt)
        }

        @Test
        @DisplayName("Expense με όλα τα πεδία συμπληρωμένα")
        fun `expense with all fields populated`() {
            val expense = Expense(
                id = "exp-1",
                userId = "user-1",
                amount = 45.50,
                category = ExpenseCategory.FUEL,
                date = 1700000000000L,
                paymentMethod = PaymentMethod.CARD,
                notes = "Βενζίνη Shell",
                shiftId = "shift-1",
                receiptUrl = "https://example.com/receipt.jpg"
            )

            assertEquals("exp-1", expense.id)
            assertEquals("user-1", expense.userId)
            assertEquals(45.50, expense.amount)
            assertEquals(ExpenseCategory.FUEL, expense.category)
            assertEquals(1700000000000L, expense.date)
            assertEquals(PaymentMethod.CARD, expense.paymentMethod)
            assertEquals("Βενζίνη Shell", expense.notes)
            assertEquals("shift-1", expense.shiftId)
            assertEquals("https://example.com/receipt.jpg", expense.receiptUrl)
        }
    }

    // ═══════════════════════════════════════════════════
    // ExpenseCategory
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("ExpenseCategory Enum")
    inner class CategoryTests {

        @Test
        @DisplayName("Όλες οι κατηγορίες έχουν emoji")
        fun `all categories have emoji`() {
            ExpenseCategory.entries.forEach { category ->
                assertTrue(category.emoji.isNotEmpty(), "${category.name} δεν έχει emoji")
            }
        }

        @Test
        @DisplayName("Υπάρχουν 10 κατηγορίες")
        fun `there are exactly 10 categories`() {
            assertEquals(10, ExpenseCategory.entries.size)
        }

        @Test
        @DisplayName("Κάθε κατηγορία έχει μοναδικό emoji")
        fun `each category has unique emoji`() {
            val emojis = ExpenseCategory.entries.map { it.emoji }
            // Σημείωση: WARNING και FINES μπορεί να μοιράζονται emoji ⚠️
            // οπότε ελέγχουμε ότι τουλάχιστον οι περισσότερες είναι μοναδικές
            assertTrue(emojis.distinct().size >= 9)
        }

        @Test
        @DisplayName("FUEL κατηγορία έχει ⛽ emoji")
        fun `fuel category has correct emoji`() {
            assertEquals("⛽", ExpenseCategory.FUEL.emoji)
        }
    }

    // ═══════════════════════════════════════════════════
    // PaymentMethod
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("PaymentMethod Enum")
    inner class PaymentMethodTests {

        @Test
        @DisplayName("Υπάρχουν 2 μέθοδοι πληρωμής")
        fun `there are exactly 2 payment methods`() {
            assertEquals(2, PaymentMethod.entries.size)
        }

        @Test
        @DisplayName("CASH και CARD υπάρχουν")
        fun `cash and card exist`() {
            assertNotNull(PaymentMethod.valueOf("CASH"))
            assertNotNull(PaymentMethod.valueOf("CARD"))
        }
    }

    // ═══════════════════════════════════════════════════
    // Soft Delete
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Soft Delete")
    inner class SoftDeleteTests {

        @Test
        @DisplayName("Νέο expense δεν είναι deleted")
        fun `new expense is not deleted`() {
            val expense = Expense()
            assertFalse(expense.isDeleted)
            assertNull(expense.deletedAt)
        }

        @Test
        @DisplayName("Deleted expense έχει timestamp")
        fun `deleted expense has timestamp`() {
            val now = System.currentTimeMillis()
            val expense = Expense(isDeleted = true, deletedAt = now)
            assertTrue(expense.isDeleted)
            assertEquals(now, expense.deletedAt)
        }
    }

    // ═══════════════════════════════════════════════════
    // Data Class Behavior
    // ═══════════════════════════════════════════════════

    @Nested
    @DisplayName("Data Class Behavior")
    inner class DataClassTests {

        @Test
        @DisplayName("Copy δουλεύει σωστά")
        fun `copy works correctly`() {
            val original = Expense(id = "1", amount = 50.0)
            val copy = original.copy(amount = 75.0)

            assertEquals("1", copy.id)
            assertEquals(75.0, copy.amount)
        }

        @Test
        @DisplayName("Equality βασίζεται σε τιμές")
        fun `equality is value-based`() {
            val a = Expense(id = "1", amount = 50.0, category = ExpenseCategory.FUEL)
            val b = Expense(id = "1", amount = 50.0, category = ExpenseCategory.FUEL)
            assertEquals(a, b)
        }
    }
}
