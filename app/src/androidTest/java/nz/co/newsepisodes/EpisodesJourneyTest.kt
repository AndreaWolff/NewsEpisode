package nz.co.newsepisodes

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
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
        // Wait for initial list
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Morning Briefing").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Listen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Morning Briefing").assertIsDisplayed()

        // Search using the stable test tag
        composeTestRule.onNodeWithTag("search_field").performTextInput("Politics")

        // Explicitly wait for the debounced search result
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Politics Now").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Politics Now").assertIsDisplayed()
        composeTestRule.onNodeWithText("Morning Briefing").assertDoesNotExist()

        // Open detail
        composeTestRule.onNodeWithText("Politics Now").performClick()

        // Wait for detail screen
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Politics Now").fetchSemanticsNodes().isNotEmpty() &&
                    composeTestRule.onAllNodesWithText("News").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Politics Now").assertIsDisplayed()
        composeTestRule.onNodeWithText("News").assertIsDisplayed()
    }

    @Test
    fun customerJourney_openEpisodeAndSave_updatesHeartAndList() {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Morning Briefing").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Morning Briefing").performClick()

        // Wait for detail to appear before interacting
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("save_button").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("save_button").performClick()

        composeTestRule.onNodeWithTag("back_button").performClick()

        // Wait for the list to reflect the saved state after navigation
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Saved").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Saved").assertIsDisplayed()
    }

    @Test
    fun customerJourney_searchNoResults_showsEmptyState() {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("search_field").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("search_field").performTextInput("xyz-not-found")

        // Wait for the debounced empty state
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithText("No results for \"xyz-not-found\"", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("No results for \"xyz-not-found\"", substring = true)
            .assertIsDisplayed()
    }
}