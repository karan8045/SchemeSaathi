package com.karan.schemesaathi.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class Category(val displayName: String) {
    SCHOLARSHIPS("Scholarships"),
    JOBS("Jobs"),
    INTERNSHIPS("Internships"),
    APPRENTICESHIPS("Apprenticeships"),
    PENSIONS("Pensions"),
    HEALTH_INSURANCE("Health Insurance"),
    FARMING("Farming & Agriculture"),
    EDUCATION("Education Support"),
    HOUSING("Housing Support"),
    SKILL_DEVELOPMENT("Skill Development"),
    SELF_EMPLOYMENT("Self-Employment & Business"),
    WOMEN_CHILD("Women & Child Welfare"),
    DISABILITY("Disability Support"),
    SENIOR_CITIZEN("Senior Citizen Support"),
    FINANCIAL_ASSISTANCE("Financial Assistance"),
    STATE_SPECIFIC("State Specific Schemes");

    companion object {
        fun fromString(value: String?): Category {
            if (value == null) return SCHOLARSHIPS
            return entries.firstOrNull { 
                it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) 
            } ?: SCHOLARSHIPS
        }
    }
}

@Serializable
enum class LocationType(val displayName: String) {
    RURAL("Rural"),
    URBAN("Urban"),
    BOTH("Both");

    companion object {
        fun fromString(value: String?): LocationType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: BOTH
        }
    }
}

@Serializable
enum class EmploymentStatus(val displayName: String) {
    UNEMPLOYED("Unemployed"),
    EMPLOYED("Employed"),
    SELF_EMPLOYED("Self-Employed"),
    STUDENT("Student"),
    RETIRED("Retired");

    companion object {
        fun fromString(value: String?): EmploymentStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: UNEMPLOYED
        }
    }
}

@Serializable
enum class IncomeRange(val displayName: String, val maxAmount: Long) {
    BELOW_1L("Below ₹1 Lakh", 100000L),
    BETWEEN_1L_2_5L("₹1 Lakh - ₹2.5 Lakhs", 250000L),
    BETWEEN_2_5L_5L("₹2.5 Lakhs - ₹5 Lakhs", 500000L),
    BETWEEN_5L_8L("₹5 Lakhs - ₹8 Lakhs", 800000L),
    ABOVE_8L("Above ₹8 Lakhs", Long.MAX_VALUE);

    companion object {
        fun fromString(value: String?): IncomeRange {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: BELOW_1L
        }
    }
}

@Serializable
enum class SocialCategory(val displayName: String) {
    GENERAL("General"),
    OBC("OBC"),
    SC("SC"),
    ST("ST"),
    EWS("EWS");

    companion object {
        fun fromString(value: String?): SocialCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: GENERAL
        }
    }
}

@Serializable
enum class EducationLevel(val displayName: String, val levelOrder: Int) {
    ILLITERATE("No Formal Education", 0),
    BELOW_10TH("Below 10th Standard", 1),
    PASS_10TH("10th Pass", 2),
    PASS_12TH("12th Pass", 3),
    ITI("ITI Certified", 4),
    DIPLOMA("Diploma Holder", 5),
    GRADUATE("Graduate / Bachelor's", 6),
    POST_GRADUATE("Post Graduate / Master's", 7);

    companion object {
        fun fromString(value: String?): EducationLevel {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: PASS_10TH
        }
    }
}

@Serializable
enum class EligibilityStatus(val label: String, val badgeColorHex: String) {
    ELIGIBLE("Eligible", "#2E7D32"),
    LIKELY_ELIGIBLE("Likely Eligible", "#0288D1"),
    MISSING_INFO("Missing Details", "#ED6C02"),
    NOT_ELIGIBLE("Not Eligible", "#D32F2F"),
    DEADLINE_PASSED("Deadline Passed", "#757575")
}

@Serializable
enum class ApplicationStatus(val displayName: String) {
    NOT_STARTED("Not Started"),
    REVIEWING_ELIGIBILITY("Reviewing Eligibility"),
    DOCUMENTS_PENDING("Documents Pending"),
    READY_TO_APPLY("Ready to Apply"),
    APPLIED("Applied"),
    SUBMITTED("Submitted"),
    COMPLETED("Completed"),
    REJECTED("Rejected"),
    EXPIRED("Expired");

    companion object {
        fun fromString(value: String?): ApplicationStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: NOT_STARTED
        }
    }
}

@Serializable
enum class DocumentStatus(val displayName: String) {
    AVAILABLE("Available"),
    MISSING("Missing"),
    EXPIRED("Expired"),
    NEEDS_VERIFICATION("Needs Verification");

    companion object {
        fun fromString(value: String?): DocumentStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MISSING
        }
    }
}

@Serializable
enum class Language(val displayName: String, val code: String) {
    EN("English", "en"),
    HI("हिन्दी", "hi"),
    BN("বাংলা", "bn"),
    TE("తెలుగు", "te"),
    MR("मराठी", "mr"),
    TA("தமிழ்", "ta"),
    GU("ગુજરાતી", "gu"),
    KN("ಕನ್ನಡ", "kn"),
    ML("മലയാളം", "ml"),
    PA("ਪੰਜਾਬੀ", "pa"),
    OR("ଓଡ଼ିଆ", "or"),
    AS("অসমীয়া", "as"),
    UR("اردو", "ur");

    companion object {
        fun fromCode(code: String?): Language {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: EN
        }
    }
}
