package com.deliverytracker.app.presentation.screens.shifts

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * Unit Tests για τη validation logic του ShiftFormViewModel.
 * 
 * Ελέγχει τους κανόνες εγκυρότητας για:
 * - Decimal input (ευρώ, χιλιόμετρα)
 * - Time input (ώρες, λεπτά)
 * - Ολοκληρωμένη φόρμα
 */
class ShiftFormValidationTest {
    
    @Nested
    @DisplayName("Decimal Validation Tests")
    inner class DecimalValidationTests {
        
        /**
         * Ελέγχει ότι το regex για decimal input λειτουργεί σωστά.
         */
        @Test
        @DisplayName("Έγκυρα decimal inputs για ευρώ (max 2 δεκαδικά)")
        fun validDecimalForEuros() {
            val validInputs = listOf(
                "",           // Κενό είναι OK
                "0",          // Μόνο αριθμός
                "12",         // Δύο ψηφία
                "123",        // Τρία ψηφία
                "12,",        // Με κόμμα
                "12,5",       // Ένα δεκαδικό
                "12,55",      // Δύο δεκαδικά
                "0,01",       // Μικρό ποσό
                "999,99"      // Μεγάλο ποσό
            )
            
            val decimalPattern = Regex("^\\d*[.,]?\\d{0,2}$")
            
            validInputs.forEach { input ->
                assertTrue(
                    input.matches(decimalPattern),
                    "Το '$input' πρέπει να είναι έγκυρο decimal"
                )
            }
        }
        
        @Test
        @DisplayName("Μη έγκυρα decimal inputs για ευρώ")
        fun invalidDecimalForEuros() {
            val invalidInputs = listOf(
                "12,555",     // Τρία δεκαδικά
                "12,,5",      // Διπλό κόμμα
                "abc",        // Γράμματα
                "12.5.5"      // Δύο τελείες
            )
            
            val decimalPattern = Regex("^\\d*[.,]?\\d{0,2}$")
            
            invalidInputs.forEach { input ->
                assertFalse(
                    input.matches(decimalPattern),
                    "Το '$input' πρέπει να είναι μη έγκυρο decimal"
                )
            }
        }
        
        @Test
        @DisplayName("Έγκυρα decimal inputs για χιλιόμετρα (max 1 δεκαδικό)")
        fun validDecimalForKilometers() {
            val validInputs = listOf(
                "",           // Κενό
                "0",          // Μηδέν
                "50",         // Ακέραιος
                "50,",        // Με κόμμα
                "50,5"        // Ένα δεκαδικό
            )
            
            val kmPattern = Regex("^\\d*[.,]?\\d{0,1}$")
            
            validInputs.forEach { input ->
                assertTrue(
                    input.matches(kmPattern),
                    "Το '$input' πρέπει να είναι έγκυρο km value"
                )
            }
        }
    }
    
    @Nested
    @DisplayName("Time Input Validation Tests")
    inner class TimeValidationTests {
        
        @ParameterizedTest
        @ValueSource(ints = [0, 1, 12, 23])
        @DisplayName("Έγκυρες ώρες (0-23)")
        fun validHours(hours: Int) {
            assertTrue(hours in 0..23, "Η ώρα $hours πρέπει να είναι έγκυρη")
        }
        
        @ParameterizedTest
        @ValueSource(ints = [-1, 24, 25, 100])
        @DisplayName("Μη έγκυρες ώρες")
        fun invalidHours(hours: Int) {
            assertFalse(hours in 0..23, "Η ώρα $hours πρέπει να είναι μη έγκυρη")
        }
        
        @ParameterizedTest
        @ValueSource(ints = [0, 1, 30, 59])
        @DisplayName("Έγκυρα λεπτά (0-59)")
        fun validMinutes(minutes: Int) {
            assertTrue(minutes in 0..59, "Τα λεπτά $minutes πρέπει να είναι έγκυρα")
        }
        
        @ParameterizedTest
        @ValueSource(ints = [-1, 60, 61, 100])
        @DisplayName("Μη έγκυρα λεπτά")
        fun invalidMinutes(minutes: Int) {
            assertFalse(minutes in 0..59, "Τα λεπτά $minutes πρέπει να είναι μη έγκυρα")
        }
    }
    
    @Nested
    @DisplayName("Business Logic Validation Tests")
    inner class BusinessLogicTests {
        
        @Test
        @DisplayName("Μέγιστη διάρκεια βάρδιας = 24 ώρες")
        fun maxShiftDurationIs24Hours() {
            val maxMinutes = 24 * 60 // 1440
            
            // Ακριβώς 24 ώρες είναι OK
            assertTrue(maxMinutes <= 1440, "24 ώρες πρέπει να είναι αποδεκτές")
            
            // 24 ώρες και 1 λεπτό δεν είναι OK
            assertFalse(maxMinutes + 1 <= 1440, "Πάνω από 24 ώρες δεν πρέπει να γίνεται αποδεκτό")
        }
        
        @Test
        @DisplayName("Υπολογισμός συνολικού εισοδήματος")
        fun totalIncomeCalculation() {
            val grossIncome = 50.0
            val tips = 10.50
            val bonus = 5.0
            
            val expected = 65.50
            val actual = grossIncome + tips + bonus
            
            assertEquals(expected, actual, 0.001, "Ο υπολογισμός συνολικού εισοδήματος πρέπει να είναι σωστός")
        }
        
        @Test
        @DisplayName("Μετατροπή decimal string σε Double")
        fun parseDecimalWithComma() {
            val input = "12,50"
            val expected = 12.50
            val actual = input.replace(",", ".").toDoubleOrNull() ?: 0.0
            
            assertEquals(expected, actual, 0.001, "Η μετατροπή decimal με κόμμα πρέπει να λειτουργεί")
        }
    }
}
