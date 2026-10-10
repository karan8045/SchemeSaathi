package com.karan.schemesaathi.data.repository

import com.karan.schemesaathi.data.local.OpportunityEntity
import com.karan.schemesaathi.data.local.SchemeSaathiDao
import com.karan.schemesaathi.data.model.Category
import com.karan.schemesaathi.data.model.EducationLevel
import com.karan.schemesaathi.data.model.SchemeOpportunity
import com.karan.schemesaathi.data.seed.SeedOpportunities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpportunityRepository @Inject constructor(
    private val dao: SchemeSaathiDao
) {

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        val count = dao.getAllOpportunities().size
        if (count == 0) {
            val seedEntities = SeedOpportunities.opportunities.map { it.toEntity() }
            dao.saveOpportunities(seedEntities)
        }
    }

    fun getAllOpportunitiesFlow(): Flow<List<SchemeOpportunity>> {
        return dao.getAllOpportunitiesFlow().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    suspend fun getOpportunityById(id: String): SchemeOpportunity? = withContext(Dispatchers.IO) {
        dao.getOpportunityById(id)?.toDomainModel()
    }

    suspend fun saveSimulatedOpportunity(opportunity: SchemeOpportunity) = withContext(Dispatchers.IO) {
        dao.saveOpportunity(opportunity.toEntity())
    }

    // Mappers
    private fun SchemeOpportunity.toEntity(): OpportunityEntity {
        return OpportunityEntity(
            id = id,
            name = name,
            category = category.name,
            description = description,
            plainLanguageSummary = plainLanguageSummary,
            minAge = minAge,
            maxAge = maxAge,
            state = state,
            district = district,
            requiredEducation = requiredEducation?.name,
            incomeLimit = incomeLimit,
            targetOccupation = targetOccupation,
            requiredSkillsJson = requiredSkills.joinToString(","),
            benefits = benefits,
            salaryOrStipend = salaryOrStipend,
            duration = duration,
            termsAndConditions = termsAndConditions,
            requiredDocumentsJson = requiredDocuments.joinToString(","),
            deadline = deadline,
            officialApplicationUrl = officialApplicationUrl,
            sourceOrganization = sourceOrganization,
            sourceUrl = sourceUrl,
            lastVerifiedDate = lastVerifiedDate,
            isCentralScheme = isCentralScheme,
            isRemote = isRemote,
            targetGender = targetGender,
            targetSocialCategory = targetSocialCategory,
            requiresFarmerStatus = requiresFarmerStatus,
            requiresStudentStatus = requiresStudentStatus,
            requiresDisabilityStatus = requiresDisabilityStatus
        )
    }

    private fun OpportunityEntity.toDomainModel(): SchemeOpportunity {
        return SchemeOpportunity(
            id = id,
            name = name,
            category = Category.fromString(category),
            description = description,
            plainLanguageSummary = plainLanguageSummary,
            minAge = minAge,
            maxAge = maxAge,
            state = state,
            district = district,
            requiredEducation = requiredEducation?.let { EducationLevel.fromString(it) },
            incomeLimit = incomeLimit,
            targetOccupation = targetOccupation,
            requiredSkills = requiredSkillsJson.split(",").filter { it.isNotBlank() },
            benefits = benefits,
            salaryOrStipend = salaryOrStipend,
            duration = duration,
            termsAndConditions = termsAndConditions,
            requiredDocuments = requiredDocumentsJson.split(",").filter { it.isNotBlank() },
            deadline = deadline,
            officialApplicationUrl = officialApplicationUrl,
            sourceOrganization = sourceOrganization,
            sourceUrl = sourceUrl,
            lastVerifiedDate = lastVerifiedDate,
            isCentralScheme = isCentralScheme,
            isRemote = isRemote,
            targetGender = targetGender,
            targetSocialCategory = targetSocialCategory,
            requiresFarmerStatus = requiresFarmerStatus,
            requiresStudentStatus = requiresStudentStatus,
            requiresDisabilityStatus = requiresDisabilityStatus
        )
    }
}
