package com.karan.schemesaathi.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    @SerialName("id") val id: String = "",
    @SerialName("full_name") val fullName: String = "",
    @SerialName("date_of_birth") val dateOfBirth: String = "", // YYYY-MM-DD
    @SerialName("gender") val gender: String = "Other",
    @SerialName("state") val state: String = "",
    @SerialName("district") val district: String = "",
    @SerialName("location_type") val locationType: String = LocationType.BOTH.name,
    @SerialName("occupation") val occupation: String = "",
    @SerialName("education_level") val educationLevel: String = EducationLevel.PASS_10TH.name,
    @SerialName("skills") val skills: List<String> = emptyList(),
    @SerialName("employment_status") val employmentStatus: String = EmploymentStatus.UNEMPLOYED.name,
    @SerialName("income_range") val incomeRange: String = IncomeRange.BELOW_1L.name,
    @SerialName("social_category") val socialCategory: String = SocialCategory.GENERAL.name,
    @SerialName("disability_status") val disabilityStatus: Boolean = false,
    @SerialName("student_status") val studentStatus: Boolean = false,
    @SerialName("farmer_status") val farmerStatus: Boolean = false,
    @SerialName("preferred_job_types") val preferredJobTypes: List<String> = emptyList(),
    @SerialName("preferred_work_locations") val preferredWorkLocations: List<String> = emptyList(),
    @SerialName("preferred_language") val preferredLanguage: String = "en",
    @SerialName("interested_categories") val interestedCategories: List<String> = emptyList(),
    @SerialName("profile_complete") val profileComplete: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
