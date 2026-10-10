package com.karan.schemesaathi.engine

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

object AgeCalculator {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /**
     * Calculates age from DOB string in ISO format (YYYY-MM-DD).
     * Returns null if DOB string is invalid or is a future date.
     */
    fun calculateAge(dobString: String?, currentDate: LocalDate = LocalDate.now()): Int? {
        if (dobString.isNullOrBlank()) return null
        return try {
            val birthDate = LocalDate.parse(dobString.trim(), formatter)
            if (birthDate.isAfter(currentDate)) {
                null // Future date is invalid
            } else {
                Period.between(birthDate, currentDate).years
            }
        } catch (_: Exception) {
            null
        }
    }

    fun isValidDob(dobString: String?, currentDate: LocalDate = LocalDate.now()): Boolean {
        if (dobString.isNullOrBlank()) return false
        return try {
            val birthDate = LocalDate.parse(dobString.trim(), formatter)
            !birthDate.isAfter(currentDate)
        } catch (_: Exception) {
            false
        }
    }
}
