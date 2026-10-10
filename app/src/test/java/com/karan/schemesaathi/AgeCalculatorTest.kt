package com.karan.schemesaathi

import com.karan.schemesaathi.engine.AgeCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class AgeCalculatorTest {

    private val referenceDate = LocalDate.of(2026, 10, 10)

    @Test
    fun calculateAge_validPastDob_returnsCorrectYears() {
        val age22 = AgeCalculator.calculateAge("2004-05-15", referenceDate)
        assertEquals(22, age22)

        val age55 = AgeCalculator.calculateAge("1971-08-20", referenceDate)
        assertEquals(55, age55)
    }

    @Test
    fun calculateAge_futureDob_returnsNull() {
        val futureAge = AgeCalculator.calculateAge("2028-01-01", referenceDate)
        assertNull(futureAge)
    }

    @Test
    fun calculateAge_invalidFormat_returnsNull() {
        assertNull(AgeCalculator.calculateAge("15-05-2004", referenceDate))
        assertNull(AgeCalculator.calculateAge("invalid-date", referenceDate))
        assertNull(AgeCalculator.calculateAge("", referenceDate))
        assertNull(AgeCalculator.calculateAge(null, referenceDate))
    }

    @Test
    fun isValidDob_checksCorrectly() {
        assertTrue(AgeCalculator.isValidDob("2000-01-01", referenceDate))
        assertFalse(AgeCalculator.isValidDob("2030-01-01", referenceDate))
        assertFalse(AgeCalculator.isValidDob("bad-format", referenceDate))
    }
}
