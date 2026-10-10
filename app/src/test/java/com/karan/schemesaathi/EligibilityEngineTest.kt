package com.karan.schemesaathi

import com.karan.schemesaathi.data.model.Category
import com.karan.schemesaathi.data.model.EducationLevel
import com.karan.schemesaathi.data.model.EligibilityStatus
import com.karan.schemesaathi.data.model.EmploymentStatus
import com.karan.schemesaathi.data.model.IncomeRange
import com.karan.schemesaathi.data.model.LocationType
import com.karan.schemesaathi.data.model.SchemeOpportunity
import com.karan.schemesaathi.data.model.SocialCategory
import com.karan.schemesaathi.data.model.UserProfile
import com.karan.schemesaathi.engine.EligibilityEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class EligibilityEngineTest {

    private val referenceDate = LocalDate.of(2026, 10, 10)

    private val sampleOpportunity = SchemeOpportunity(
        id = "test_01",
        name = "Central Higher Education Scholarship",
        category = Category.SCHOLARSHIPS,
        description = "Test scholarship for students",
        plainLanguageSummary = "Test summary",
        minAge = 17,
        maxAge = 25,
        state = "All India",
        requiredEducation = EducationLevel.PASS_12TH,
        incomeLimit = 450000L,
        benefits = "₹12,000/yr",
        deadline = "2026-11-30",
        officialApplicationUrl = "https://scholarships.gov.in",
        sourceOrganization = "Ministry of Education",
        sourceUrl = "https://scholarships.gov.in",
        lastVerifiedDate = "2026-09-01",
        requiresStudentStatus = true
    )

    @Test
    fun evaluate_eligibleUser_returnsEligibleStatus() {
        val user = UserProfile(
            id = "u1",
            fullName = "Kavya Sharma",
            dateOfBirth = "2004-05-15", // 22 years old
            gender = "Female",
            state = "Maharashtra",
            educationLevel = EducationLevel.PASS_12TH.name,
            incomeRange = IncomeRange.BETWEEN_1L_2_5L.name,
            studentStatus = true,
            profileComplete = true
        )

        val result = EligibilityEngine.evaluate(user, sampleOpportunity, referenceDate)
        assertEquals(EligibilityStatus.ELIGIBLE, result.status)
        assertTrue(result.matchedReasons.isNotEmpty())
        assertTrue(result.unmatchedReasons.isEmpty())
    }

    @Test
    fun evaluate_passedDeadline_returnsDeadlinePassedStatus() {
        val pastOpportunity = sampleOpportunity.copy(deadline = "2025-01-01")
        val user = UserProfile(dateOfBirth = "2004-05-15")

        val result = EligibilityEngine.evaluate(user, pastOpportunity, referenceDate)
        assertEquals(EligibilityStatus.DEADLINE_PASSED, result.status)
    }

    @Test
    fun evaluate_incompleteUser_returnsMissingInfoStatus() {
        val user = UserProfile(fullName = "No DOB")
        val result = EligibilityEngine.evaluate(user, sampleOpportunity, referenceDate)
        assertEquals(EligibilityStatus.MISSING_INFO, result.status)
    }

    @Test
    fun evaluate_ageMismatch_returnsNotEligible() {
        val oldUser = UserProfile(
            dateOfBirth = "1980-01-01", // 46 years old (limit is 25)
            studentStatus = true,
            educationLevel = EducationLevel.PASS_12TH.name
        )

        val result = EligibilityEngine.evaluate(oldUser, sampleOpportunity, referenceDate)
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, result.status)
        assertTrue(result.unmatchedReasons.any { it.contains("maximum age limit") })
    }

    @Test
    fun evaluate_stateMismatch_returnsNotEligible() {
        val mhScheme = sampleOpportunity.copy(state = "Maharashtra")
        val upUser = UserProfile(
            dateOfBirth = "2004-05-15",
            state = "Uttar Pradesh",
            educationLevel = EducationLevel.PASS_12TH.name,
            studentStatus = true
        )

        val result = EligibilityEngine.evaluate(upUser, mhScheme, referenceDate)
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, result.status)
        assertTrue(result.unmatchedReasons.any { it.contains("specific to Maharashtra") })
    }
}
