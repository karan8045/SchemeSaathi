package com.karan.schemesaathi

import com.karan.schemesaathi.data.model.Category
import com.karan.schemesaathi.data.model.Language
import com.karan.schemesaathi.data.model.NotificationPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationFilterTest {

    @Test
    fun notificationPreferences_defaultEnabledCategories() {
        val prefs = NotificationPreferences(userId = "user_1")
        assertTrue(prefs.scholarships)
        assertTrue(prefs.jobs)
        assertTrue(prefs.internships)
        assertTrue(prefs.pensions)
        assertTrue(prefs.enabled)
    }

    @Test
    fun language_fromCode_fallbackToEnglish() {
        assertEquals(Language.EN, Language.fromCode("en"))
        assertEquals(Language.HI, Language.fromCode("hi"))
        assertEquals(Language.MR, Language.fromCode("mr"))
        assertEquals(Language.EN, Language.fromCode("unknown_code"))
        assertEquals(Language.EN, Language.fromCode(null))
    }

    @Test
    fun category_fromString_parsesCorrectly() {
        assertEquals(Category.SCHOLARSHIPS, Category.fromString("Scholarships"))
        assertEquals(Category.JOBS, Category.fromString("jobs"))
        assertEquals(Category.FARMING, Category.fromString("Farming & Agriculture"))
        assertEquals(Category.SCHOLARSHIPS, Category.fromString("Unknown"))
    }
}
