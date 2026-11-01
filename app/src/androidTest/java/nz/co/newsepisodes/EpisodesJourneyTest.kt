package nz.co.newsepisodes

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EpisodesJourneyTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun customerJourney_searchAndOpenEpisode_showsDetail() {
        // List is visible
        composeTestRule.onNodeWithText("Listen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Morning Briefing").assertIsDisplayed()

        // Search
        composeTestRule.onNodeWithText("Search episodes").performTextInput("Politics")
        composeTestRule.onNodeWithText("Politics Now").assertIsDisplayed()
        composeTestRule.onNodeWithText("Morning Briefing").assertDoesNotExist()

        // Open detail
        composeTestRule.onNodeWithText("Politics Now").performClick()
        composeTestRule.onNodeWithText("Politics Now").assertIsDisplayed()
        composeTestRule.onNodeWithText("News").assertIsDisplayed()
    }

    @Test
    fun customerJourney_openEpisodeAndSave_updatesHeartAndList() {
        composeTestRule.onNodeWithText("Morning Briefing").performClick()

        composeTestRule.onNodeWithTag("save_button").performClick()

        composeTestRule.onNodeWithTag("back_button").performClick()

        composeTestRule.onNodeWithText("Saved").assertIsDisplayed()
    }

    @Test
    fun customerJourney_searchNoResults_showsEmptyState() {
        composeTestRule.onNodeWithTag("search_field").performTextInput("xyz-not-found")

        composeTestRule
            .onNodeWithText("No results for \"query\"", substring = true)
            .assertIsDisplayed()
    }
}