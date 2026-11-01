package nz.co.newsepisodes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import nz.co.newsepisodes.common.usecase.GetEpisodeUseCase
import nz.co.newsepisodes.common.usecase.ToggleSavedUseCase
import nz.co.newsepisodes.details.EpisodeDetailUiState
import nz.co.newsepisodes.details.EpisodeDetailsViewModel
import nz.co.newsepisodes.common.model.Episode
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class EpisodeDetailsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var getEpisode: GetEpisodeUseCase

    @Mock
    private lateinit var toggleSaved: ToggleSavedUseCase

    private lateinit var viewModel: EpisodeDetailsViewModel

    private val sampleEpisode = Episode(
        "1", "Morning Briefing", "News",
        "Today's top Australian stories", 12, "2026-10-30"
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialLoad_emitsSuccess() = runTest {
        whenever(getEpisode("1")).thenReturn(flowOf(sampleEpisode))

        viewModel = EpisodeDetailsViewModel("1", getEpisode, toggleSaved)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is EpisodeDetailUiState.Success)
        assertEquals("Morning Briefing", (state as EpisodeDetailUiState.Success).episode.title)
        verify(getEpisode).invoke("1")
    }

    @Test
    fun onSaveClicked_callsToggleSaved() = runTest {
        whenever(getEpisode("1")).thenReturn(flowOf(sampleEpisode))

        viewModel = EpisodeDetailsViewModel("1", getEpisode, toggleSaved)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSavedClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(toggleSaved).invoke("1")
    }
}