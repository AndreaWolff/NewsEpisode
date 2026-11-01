package nz.co.newsepisodes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import nz.co.newsepisodes.common.di.CoroutineDispatchers
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

    private val dispatchers = object : CoroutineDispatchers {
        override val main = testDispatcher
        override val io = testDispatcher
        override val default = testDispatcher
    }

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
    fun initialLoad_emitsSuccess() = runTest(testDispatcher) {
        whenever(getEpisode("1")).thenReturn(sampleEpisode)

        viewModel = EpisodeDetailsViewModel("1", getEpisode, toggleSaved, dispatchers)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.status is EpisodeDetailUiState.DetailStatus.Success)
        assertEquals("Morning Briefing", (state.status as EpisodeDetailUiState.DetailStatus.Success).episode.title)
        verify(getEpisode).invoke("1")
    }

    @Test
    fun onSaveClicked_callsToggleSaved() = runTest {
        whenever(getEpisode("1")).thenReturn(sampleEpisode)

        viewModel = EpisodeDetailsViewModel("1", getEpisode, toggleSaved, dispatchers)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSavedClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(toggleSaved).invoke("1")
    }

    @Test
    fun initialLoad_error_emitsErrorState() = runTest(testDispatcher) {
        whenever(getEpisode("1")).thenThrow(RuntimeException("Not found"))

        viewModel = EpisodeDetailsViewModel("1", getEpisode, toggleSaved, dispatchers)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.status is EpisodeDetailUiState.DetailStatus.Error)
        assertEquals("Not found", (state.status as EpisodeDetailUiState.DetailStatus.Error).message)
    }
}