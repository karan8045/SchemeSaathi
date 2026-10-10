package com.karan.schemesaathi.data.seed

import com.karan.schemesaathi.data.model.EducationLevel
import com.karan.schemesaathi.data.model.EmploymentStatus
import com.karan.schemesaathi.data.model.IncomeRange
import com.karan.schemesaathi.data.model.LocationType
import com.karan.schemesaathi.data.model.SocialCategory
import com.karan.schemesaathi.data.model.UserProfile

object DemoPersonas {

    val personaStudent = UserProfile(
        id = "persona_student",
        fullName = "Kavya Sharma",
        dateOfBirth = "2004-05-15", // 22 years old
        gender = "Female",
        state = "Maharashtra",
        district = "Pune",
        locationType = LocationType.URBAN.name,
        occupation = "College Student",
        educationLevel = EducationLevel.PASS_12TH.name,
        skills = listOf("Python", "Communication", "Data Entry"),
        employmentStatus = EmploymentStatus.STUDENT.name,
        incomeRange = IncomeRange.BETWEEN_1L_2_5L.name,
        socialCategory = SocialCategory.OBC.name,
        disabilityStatus = false,
        studentStatus = true,
        farmerStatus = false,
        preferredJobTypes = listOf("Internship", "Remote", "Part-Time"),
        preferredWorkLocations = listOf("Pune", "Remote", "Mumbai"),
        preferredLanguage = "en",
        interestedCategories = listOf("Scholarships", "Internships", "Skill Development"),
        profileComplete = true
    )

    val personaFarmer = UserProfile(
        id = "persona_farmer",
        fullName = "Ramesh Patil",
        dateOfBirth = "1971-08-20", // 55 years old
        gender = "Male",
        state = "Maharashtra",
        district = "Nashik",
        locationType = LocationType.RURAL.name,
        occupation = "Agriculture / Farming",
        educationLevel = EducationLevel.PASS_10TH.name,
        skills = listOf("Crop Cultivation", "Irrigation", "Tractor Operation"),
        employmentStatus = EmploymentStatus.SELF_EMPLOYED.name,
        incomeRange = IncomeRange.BELOW_1L.name,
        socialCategory = SocialCategory.GENERAL.name,
        disabilityStatus = false,
        studentStatus = false,
        farmerStatus = true,
        preferredJobTypes = listOf("Agricultural Subsidy", "Equipment Grant"),
        preferredWorkLocations = listOf("Nashik"),
        preferredLanguage = "mr",
        interestedCategories = listOf("Farming & Agriculture", "Pensions", "Health Insurance"),
        profileComplete = true
    )

    val personaWidow = UserProfile(
        id = "persona_widow",
        fullName = "Lakshmi Devi",
        dateOfBirth = "1961-03-10", // 65 years old
        gender = "Female",
        state = "Uttar Pradesh",
        district = "Varanasi",
        locationType = LocationType.RURAL.name,
        occupation = "Homemaker / Retired",
        educationLevel = EducationLevel.ILLITERATE.name,
        skills = listOf("Handicraft", "Tailoring"),
        employmentStatus = EmploymentStatus.RETIRED.name,
        incomeRange = IncomeRange.BELOW_1L.name,
        socialCategory = SocialCategory.SC.name,
        disabilityStatus = false,
        studentStatus = false,
        farmerStatus = false,
        preferredJobTypes = listOf("Pension", "Healthcare"),
        preferredWorkLocations = listOf("Varanasi"),
        preferredLanguage = "hi",
        interestedCategories = listOf("Pensions", "Health Insurance", "Housing Support"),
        profileComplete = true
    )

    val personaGraduate = UserProfile(
        id = "persona_graduate",
        fullName = "Anand Kumar",
        dateOfBirth = "2002-11-10", // 23 years old
        gender = "Male",
        state = "Karnataka",
        district = "Bengaluru Urban",
        locationType = LocationType.URBAN.name,
        occupation = "Job Seeker",
        educationLevel = EducationLevel.GRADUATE.name,
        skills = listOf("Java", "Kotlin", "Web Development"),
        employmentStatus = EmploymentStatus.UNEMPLOYED.name,
        incomeRange = IncomeRange.BETWEEN_1L_2_5L.name,
        socialCategory = SocialCategory.GENERAL.name,
        disabilityStatus = false,
        studentStatus = false,
        farmerStatus = false,
        preferredJobTypes = listOf("Full Time", "Apprenticeship", "IT Job"),
        preferredWorkLocations = listOf("Bengaluru", "Remote"),
        preferredLanguage = "kn",
        interestedCategories = listOf("Jobs", "Apprenticeships", "State Specific Schemes"),
        profileComplete = true
    )

    val personaPwD = UserProfile(
        id = "persona_pwd",
        fullName = "Suresh Verma",
        dateOfBirth = "1998-04-25", // 28 years old
        gender = "Male",
        state = "Delhi",
        district = "Central Delhi",
        locationType = LocationType.URBAN.name,
        occupation = "Office Assistant",
        educationLevel = EducationLevel.PASS_12TH.name,
        skills = listOf("Computer Operations", "Customer Support"),
        employmentStatus = EmploymentStatus.UNEMPLOYED.name,
        incomeRange = IncomeRange.BELOW_1L.name,
        socialCategory = SocialCategory.OBC.name,
        disabilityStatus = true,
        studentStatus = false,
        farmerStatus = false,
        preferredJobTypes = listOf("Government Job", "Assistive Aid"),
        preferredWorkLocations = listOf("Delhi NCR"),
        preferredLanguage = "hi",
        interestedCategories = listOf("Disability Support", "Jobs", "Skill Development"),
        profileComplete = true
    )

    val personaLowIncomeFamily = UserProfile(
        id = "persona_low_income",
        fullName = "Sunita Devi",
        dateOfBirth = "1988-09-12", // 38 years old
        gender = "Female",
        state = "Bihar",
        district = "Patna",
        locationType = LocationType.RURAL.name,
        occupation = "Self Help Group Worker",
        educationLevel = EducationLevel.PASS_10TH.name,
        skills = listOf("Tailoring", "Self Help Group Leader"),
        employmentStatus = EmploymentStatus.SELF_EMPLOYED.name,
        incomeRange = IncomeRange.BELOW_1L.name,
        socialCategory = SocialCategory.EWS.name,
        disabilityStatus = false,
        studentStatus = false,
        farmerStatus = false,
        preferredJobTypes = listOf("Micro Loan", "Housing Grant"),
        preferredWorkLocations = listOf("Patna"),
        preferredLanguage = "hi",
        interestedCategories = listOf("Women & Child Welfare", "Self-Employment & Business", "Housing Support"),
        profileComplete = true
    )

    val allPersonas = listOf(
        personaStudent,
        personaFarmer,
        personaWidow,
        personaGraduate,
        personaPwD,
        personaLowIncomeFamily
    )
}
