package com.karan.schemesaathi

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.karan.schemesaathi.ui.auth.MandatoryOnboardingScreen
import com.karan.schemesaathi.ui.theme.SchemeSaathiTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMandatoryOnboarding_DisplaysPrivacyInfoAndTitle() {
        composeTestRule.setContent {
            SchemeSaathiTheme {
                MandatoryOnboardingScreen(
                    initialUserId = "test_user_1",
                    onOnboardingCompleted = {}
                )
            }
        }

        // Verify Title and Mandatory Privacy Information Banner
        composeTestRule.onNodeWithText("Mandatory Onboarding (1/3)").assertExists()
        composeTestRule.onNodeWithText("Step 1: Personal & Location Information").assertExists()
    }
}
