package com.karan.schemesaathi.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SchemeOpportunity(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("category") val category: Category,
    @SerialName("description") val description: String,
    @SerialName("plain_language_summary") val plainLanguageSummary: String,
    @SerialName("min_age") val minAge: Int? = null,
    @SerialName("max_age") val maxAge: Int? = null,
    @SerialName("state") val state: String? = "All India",
    @SerialName("district") val district: String? = null,
    @SerialName("required_education") val requiredEducation: EducationLevel? = null,
    @SerialName("income_limit") val incomeLimit: Long? = null,
    @SerialName("target_occupation") val targetOccupation: String? = null,
    @SerialName("required_skills") val requiredSkills: List<String> = emptyList(),
    @SerialName("benefits") val benefits: String,
    @SerialName("salary_or_stipend") val salaryOrStipend: String? = null,
    @SerialName("duration") val duration: String? = null,
    @SerialName("terms_and_conditions") val termsAndConditions: String = "",
    @SerialName("required_documents") val requiredDocuments: List<String> = emptyList(),
    @SerialName("deadline") val deadline: String? = null, // YYYY-MM-DD
    @SerialName("official_application_url") val officialApplicationUrl: String,
    @SerialName("source_organization") val sourceOrganization: String,
    @SerialName("source_url") val sourceUrl: String,
    @SerialName("last_verified_date") val lastVerifiedDate: String,
    @SerialName("is_central_scheme") val isCentralScheme: Boolean = true,
    @SerialName("is_remote") val isRemote: Boolean = false,
    @SerialName("target_gender") val targetGender: String? = null, // All, Female, Male
    @SerialName("target_social_category") val targetSocialCategory: String? = null,
    @SerialName("requires_farmer_status") val requiresFarmerStatus: Boolean = false,
    @SerialName("requires_student_status") val requiresStudentStatus: Boolean = false,
    @SerialName("requires_disability_status") val requiresDisabilityStatus: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
