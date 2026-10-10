package com.karan.schemesaathi.data.remote

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.karan.schemesaathi.BuildConfig
import com.karan.schemesaathi.data.model.SchemeOpportunity
import com.karan.schemesaathi.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiAssistantManager @Inject constructor() {

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    suspend fun askSaathi(
        prompt: String,
        opportunity: SchemeOpportunity? = null,
        userProfile: UserProfile? = null,
        language: String = "en"
    ): Result<String> = withContext(Dispatchers.IO) {
        
        // Use real Gemini API if key is configured
        if (!BuildConfig.GEMINI_API_KEY.contains("dummy")) {
            try {
                val systemContext = """
                    You are 'Saathi', an expert AI assistant inside the 'Scheme Saathi' Android app.
                    Your job is to help citizens in India understand government schemes, scholarships, jobs, and pensions.
                    Always reply in the user's preferred language code: '$language'.
                    Keep answers short, highly accurate, formatting with bullet points.
                    If asked to submit an application, clarify that you cannot submit, but they can click 'Proceed to Official Portal'.
                    ${opportunity?.let { "The user is currently viewing: ${it.name}. Required docs: ${it.requiredDocuments.joinToString()}" } ?: ""}
                    ${userProfile?.let { "The user's profile: State ${it.state}, Income ${it.incomeRange}." } ?: ""}
                """.trimIndent()

                val response = generativeModel.generateContent(
                    content {
                        text(systemContext)
                        text("\n\nUser Question: $prompt")
                    }
                )

                if (!response.text.isNullOrBlank()) {
                    return@withContext Result.success(response.text!!)
                }
            } catch (e: Exception) {
                // Fallback to local rules on error
            }
        }

        // Local intelligent mock response for Demo mode, offline, or fallback
        val lowercasePrompt = prompt.lowercase()
        val response = when {
            lowercasePrompt.contains("document") || lowercasePrompt.contains("dastavez") || lowercasePrompt.contains("कागजात") -> {
                val docs = opportunity?.requiredDocuments?.joinToString("\n• ") ?: "Aadhaar Card, Income Certificate, Domicile Certificate, Bank Passbook, Passport Photo"
                if (language == "hi") {
                    "आवश्यक दस्तावेज (Required Documents):\n• $docs\n\nसुझाव: सुनिश्चित करें कि आपका नाम आधार कार्ड और बैंक पासबुक में एक समान हो।"
                } else {
                    "Required Documents for application:\n• $docs\n\nTip: Ensure your name on your Aadhaar card matches your Bank Passbook exactly."
                }
            }
            lowercasePrompt.contains("eligible") || lowercasePrompt.contains("पात्र") -> {
                val name = opportunity?.name ?: "this scheme"
                if (userProfile != null) {
                    "Based on your profile (${userProfile.fullName}, ${userProfile.state}), you fit the primary criteria for $name. Please verify your income certificate and age limit before applying on the official portal."
                } else {
                    "Eligibility for $name depends on your Age, State residency, Family Income, and Category. Please complete your profile to see your exact match status."
                }
            }
            lowercasePrompt.contains("deadline") || lowercasePrompt.contains("last date") || lowercasePrompt.contains("अंतिम तिथि") -> {
                val deadline = opportunity?.deadline ?: "Open throughout the year"
                "The official deadline for this application is: $deadline. We recommend completing your document checklist at least 5 days prior to avoid server rush."
            }
            lowercasePrompt.contains("paid") || lowercasePrompt.contains("stipend") || lowercasePrompt.contains("salary") || lowercasePrompt.contains("छात्रवृत्ति") || lowercasePrompt.contains("पैसा") -> {
                val benefit = opportunity?.benefits ?: opportunity?.salaryOrStipend ?: "Direct financial grant"
                "Financial Benefit Details:\n$benefit\n\nPayments are directly credited to your Aadhaar-seeded bank account via DBTL."
            }
            lowercasePrompt.contains("hindi") || lowercasePrompt.contains("हिंदी") -> {
                "जी हां! स्कीम साथी में आप सभी सरकारी योजनाओं, नौकरियों और छात्रवृत्तियों की जानकारी सरल हिंदी में प्राप्त कर सकते हैं।"
            }
            else -> {
                val oppName = opportunity?.name ?: "this public benefit opportunity"
                if (language == "hi") {
                    "$oppName की आवेदन प्रक्रिया के लिए आपके पास सही दस्तावेज और पात्रता का होना आवश्यक है। आप आधिकारिक पोर्टल पर जाने से पहले हमारे आवेदन सहायक का उपयोग कर सकते हैं।"
                } else {
                    "For applying to $oppName, ensure you have all required certificates ready. You can use our Scheme Saathi Application Assistant to prefill your form before opening the official government portal."
                }
            }
        }

        Result.success(response)
    }
}
