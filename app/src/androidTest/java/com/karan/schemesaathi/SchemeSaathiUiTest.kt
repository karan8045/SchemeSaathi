package com.karan.schemesaathi

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.karan.schemesaathi.data.model.Category
import com.karan.schemesaathi.data.model.EligibilityStatus
import com.karan.schemesaathi.data.model.SchemeOpportunity
import com.karan.schemesaathi.ui.components.AppHeader
import com.karan.schemesaathi.ui.components.EligibilityBadge
import com.karan.schemesaathi.ui.components.OpportunityCard
import com.karan.schemesaathi.ui.theme.SchemeSaathiTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SchemeSaathiUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun appHeader_displaysBrandingAndSearch() {
        composeTestRule.setContent {
            SchemeSaathiTheme {
                AppHeader(
                    userName = "Kavya",
                    selectedLanguage = "en",
                    onLanguageClick = {},
                    searchQuery = "",
                    onSearchQueryChange = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Namaste, Kavya! 🙏").assertIsDisplayed()
        composeTestRule.onNodeWithText("English").assertIsDisplayed()
    }

    @Test
    fun opportunityCard_displaysTitleAndBadge() {
        val sampleOpp = SchemeOpportunity(
            id = "test_01",
            name = "PM Central Sector Scholarship",
            category = Category.SCHOLARSHIPS,
            description = "Test description",
            plainLanguageSummary = "Test plain language summary",
            benefits = "₹12,000 / year",
            officialApplicationUrl = "https://scholarships.gov.in",
            sourceOrganization = "Govt of India",
            sourceUrl = "https://scholarships.gov.in",
            lastVerifiedDate = "2026-09-01"
        )

        composeTestRule.setContent {
            SchemeSaathiTheme {
                OpportunityCard(
                    opportunity = sampleOpp,
                    eligibilityStatus = EligibilityStatus.ELIGIBLE,
                    isSaved = false,
                    onSaveClick = {},
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("PM Central Sector Scholarship").assertIsDisplayed()
        composeTestRule.onNodeWithText("Scholarships").assertIsDisplayed()
        composeTestRule.onNodeWithText("Eligible").assertIsDisplayed()
    }

    @Test
    fun eligibilityBadge_displaysCorrectText() {
        composeTestRule.setContent {
            SchemeSaathiTheme {
                EligibilityBadge(status = EligibilityStatus.LIKELY_ELIGIBLE)
            }
        }

        composeTestRule.onNodeWithText("Likely Eligible").assertIsDisplayed()
    }
}
