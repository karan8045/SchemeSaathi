package com.karan.schemesaathi.util

data class Language(
    val code: String,
    val nativeName: String,
    val englishName: String
)

object LanguageManager {

    val supportedLanguages = listOf(
        Language("en", "English", "English"),
        Language("hi", "हिन्दी", "Hindi"),
        Language("as", "অসমীয়া", "Assamese"),
        Language("bn", "বাংলা", "Bengali"),
        Language("gu", "ગુજરાતી", "Gujarati"),
        Language("kn", "ಕನ್ನಡ", "Kannada"),
        Language("ml", "മലയാളം", "Malayalam"),
        Language("mr", "मराठी", "Marathi"),
        Language("or", "ଓଡ଼ିଆ", "Odia"),
        Language("pa", "ਪੰਜਾਬੀ", "Punjabi"),
        Language("ta", "தமிழ்", "Tamil"),
        Language("te", "తెలుగు", "Telugu"),
        Language("ur", "اردو", "Urdu")
    )

    private val translations = mapOf(
        "hi" to mapOf(
            "app_name" to "स्कीम साथी",
            "home" to "होम",
            "explore" to "खोजें",
            "saved" to "सेव किए गए",
            "alerts" to "अलर्ट",
            "profile" to "प्रोफ़ाइल",
            "ask_saathi" to "साथी से पूछें",
            "apply_now" to "अभी आवेदन करें",
            "eligible" to "पात्र हैं",
            "likely_eligible" to "संभावित पात्र",
            "not_eligible" to "पात्र नहीं हैं",
            "required_documents" to "आवश्यक दस्तावेज़",
            "deadline" to "अंतिम तिथि",
            "official_website" to "आधिकारिक वेबसाइट",
            "search_placeholder" to "योजनाएँ, छात्रवृत्ति, नौकरी खोजें...",
            "categories" to "श्रेणियाँ",
            "connect_telegram" to "टेलीग्राम से जुड़ें",
            "simulated_alert" to "नया अवसर सिम्युलेट करें"
        ),
        "mr" to mapOf(
            "app_name" to "स्कीम साथी",
            "home" to "होम",
            "explore" to "शोधा",
            "saved" to "जतन केलेले",
            "alerts" to "अलर्ट्स",
            "profile" to "प्रोफाइल",
            "ask_saathi" to "साथीला विचारा",
            "apply_now" to "आत्ता अर्ज करा",
            "eligible" to "पात्र आहात",
            "required_documents" to "आवश्यक कागदपत्रे",
            "deadline" to "अंतिम मुदत"
        ),
        "ta" to mapOf(
            "app_name" to "ஸ்கீம் சாதி",
            "home" to "முகப்பு",
            "explore" to "ஆராய்க",
            "saved" to "சேமிக்கப்பட்டவை",
            "alerts" to "அறிவிப்புகள்",
            "profile" to "சுயவிவரம்",
            "ask_saathi" to "சாதியிடம் கேட்க"
        ),
        "te" to mapOf(
            "app_name" to "స్కీమ్ సాథీ",
            "home" to "హోమ్",
            "explore" to "అన్వేషించండి",
            "saved" to "సేవ్ చేసినవి",
            "alerts" to "అలర్ట్లు",
            "profile" to "ప్రొఫైల్",
            "ask_saathi" to "సాథీని అడగండి"
        )
    )

    fun getString(key: String, languageCode: String, fallback: String = key): String {
        return translations[languageCode]?.get(key) ?: fallback
    }
}
