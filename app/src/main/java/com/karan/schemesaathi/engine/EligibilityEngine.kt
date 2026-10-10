package com.karan.schemesaathi.engine

import com.karan.schemesaathi.data.model.EducationLevel
import com.karan.schemesaathi.data.model.EligibilityStatus
import com.karan.schemesaathi.data.model.IncomeRange
import com.karan.schemesaathi.data.model.SchemeOpportunity
import com.karan.schemesaathi.data.model.UserProfile
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class EligibilityResult(
    val status: EligibilityStatus,
    val explanation: String,
    val matchedReasons: List<String>,
    val unmatchedReasons: List<String>
)

object EligibilityEngine {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun evaluate(user: UserProfile?, scheme: SchemeOpportunity, currentDate: LocalDate = LocalDate.now()): EligibilityResult {
        val matched = mutableListOf<String>()
        val unmatched = mutableListOf<String>()

        // 1. Check Deadline
        if (!scheme.deadline.isNullOrBlank()) {
            try {
                val deadlineDate = LocalDate.parse(scheme.deadline, dateFormatter)
                if (currentDate.isAfter(deadlineDate)) {
                    return EligibilityResult(
                        status = EligibilityStatus.DEADLINE_PASSED,
                        explanation = "The application deadline for this scheme (${scheme.deadline}) has already passed.",
                        matchedReasons = emptyList(),
                        unmatchedReasons = listOf("Application deadline passed on ${scheme.deadline}")
                    )
                }
            } catch (_: Exception) {
                // If invalid deadline string, ignore
            }
        }

        // 2. Check if user profile is present
        if (user == null || user.dateOfBirth.isBlank()) {
            return EligibilityResult(
                status = EligibilityStatus.MISSING_INFO,
                explanation = "Please complete your profile details (Date of Birth, State, Education) to check exact eligibility.",
                matchedReasons = emptyList(),
                unmatchedReasons = listOf("Profile incomplete or missing Date of Birth")
            )
        }

        // 3. Age Calculation & Check
        val age = AgeCalculator.calculateAge(user.dateOfBirth, currentDate)
        if (age == null) {
            unmatched.add("Invalid Date of Birth provided")
        } else {
            val minAge = scheme.minAge
            val maxAge = scheme.maxAge

            if (minAge != null && age < minAge) {
                unmatched.add("You are $age years old, but minimum age requirement is $minAge years.")
            } else if (maxAge != null && age > maxAge) {
                unmatched.add("You are $age years old, but maximum age limit is $maxAge years.")
            } else {
                matched.add("Age $age fits the requirement (${minAge ?: 0} to ${maxAge ?: 100} years).")
            }
        }

        // 4. State & District Check
        val schemeState = scheme.state
        if (!schemeState.isNullOrBlank() && !schemeState.equals("All India", ignoreCase = true)) {
            if (user.state.isNotBlank() && user.state.equals(schemeState, ignoreCase = true)) {
                matched.add("Resident of $schemeState.")
            } else if (user.state.isBlank()) {
                unmatched.add("State not specified in profile (requires $schemeState).")
            } else {
                unmatched.add("This scheme is specific to ${schemeState}, but your state is ${user.state}.")
            }
        } else {
            matched.add("Available for all States in India.")
        }

        // 5. Gender Check
        val targetGender = scheme.targetGender
        if (!targetGender.isNullOrBlank() && !targetGender.equals("All", ignoreCase = true)) {
            if (user.gender.equals(targetGender, ignoreCase = true)) {
                matched.add("Gender matches ($targetGender).")
            } else {
                unmatched.add("Specifically designed for $targetGender applicants.")
            }
        }

        // 6. Social Category Check
        val targetCategory = scheme.targetSocialCategory
        if (!targetCategory.isNullOrBlank() && !targetCategory.equals("All", ignoreCase = true)) {
            if (user.socialCategory.equals(targetCategory, ignoreCase = true)) {
                matched.add("Social category matches ($targetCategory).")
            } else {
                unmatched.add("Targeted for $targetCategory category (your category: ${user.socialCategory}).")
            }
        }

        // 7. Student Status
        if (scheme.requiresStudentStatus) {
            if (user.studentStatus) {
                matched.add("Currently a student.")
            } else {
                unmatched.add("Requires active student status.")
            }
        }

        // 8. Farmer Status
        if (scheme.requiresFarmerStatus) {
            if (user.farmerStatus) {
                matched.add("Registered/Active Farmer.")
            } else {
                unmatched.add("Requires farmer status.")
            }
        }

        // 9. Disability Status
        if (scheme.requiresDisabilityStatus) {
            if (user.disabilityStatus) {
                matched.add("Person with Disability (PwD) criteria met.")
            } else {
                unmatched.add("Requires Person with Disability (PwD) certification.")
            }
        }

        // 10. Education Requirement
        val reqEdu = scheme.requiredEducation
        if (reqEdu != null) {
            val userEdu = EducationLevel.fromString(user.educationLevel)
            if (userEdu.levelOrder >= reqEdu.levelOrder) {
                matched.add("Education level (${userEdu.displayName}) meets minimum requirement (${reqEdu.displayName}).")
            } else {
                unmatched.add("Requires at least ${reqEdu.displayName} (your education: ${userEdu.displayName}).")
            }
        }

        // 11. Income Limit
        val incomeLimit = scheme.incomeLimit
        if (incomeLimit != null) {
            val userIncomeRange = IncomeRange.fromString(user.incomeRange)
            if (userIncomeRange.maxAmount <= incomeLimit) {
                matched.add("Family income (${userIncomeRange.displayName}) is within the limit of ₹${incomeLimit / 100000} Lakh/yr.")
            } else {
                unmatched.add("Income exceeds the ₹${incomeLimit / 100000} Lakh annual income limit.")
            }
        }

        // Final Outcome Determination
        return when {
            unmatched.isEmpty() -> {
                val ageText = age?.let { "$it years old" } ?: "of eligible age"
                val locationText = if (user.state.isNotBlank()) "live in ${user.state}" else "live in India"
                val summaryText = "You appear eligible because you are $ageText and $locationText. Remember to verify income documents and submit required certificates on the official portal."
                EligibilityResult(
                    status = EligibilityStatus.ELIGIBLE,
                    explanation = summaryText,
                    matchedReasons = matched,
                    unmatchedReasons = emptyList()
                )
            }
            matched.isNotEmpty() && unmatched.size <= 2 && unmatched.any { it.contains("specify", ignoreCase = true) || it.contains("income", ignoreCase = true) } -> {
                EligibilityResult(
                    status = EligibilityStatus.LIKELY_ELIGIBLE,
                    explanation = "You match most criteria (${matched.size} matched), but need to confirm ${unmatched.first().lowercase()}.",
                    matchedReasons = matched,
                    unmatchedReasons = unmatched
                )
            }
            else -> {
                EligibilityResult(
                    status = EligibilityStatus.NOT_ELIGIBLE,
                    explanation = "You currently do not meet all requirements for this opportunity.",
                    matchedReasons = matched,
                    unmatchedReasons = unmatched
                )
            }
        }
    }
}
