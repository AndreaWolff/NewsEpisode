package nz.co.newsepisodes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import nz.co.newsepisodes.common.usecase.GetEpisodesUseCase
import nz.co.newsepisodes.common.usecase.SearchEpisodesUseCase
import nz.co.newsepisodes.common.model.Episode
import nz.co.newsepisodes.episode.EpisodeUiState
import nz.co.newsepisodes.episode.EpisodeViewModel
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class EpisodeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var getEpisodes: GetEpisodesUseCase

    @Mock
    private lateinit var searchEpisodes: SearchEpisodesUseCase

    private lateinit var viewModel: EpisodeViewModel

    private val sampleEpisodes = listOf(
        Episode("1", "A", "Show", "Desc", 10, "2026-01-01"),
        Episode("2", "B", "Show", "Desc", 20, "2026-01-02")
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
        whenever(getEpisodes()).thenReturn(flowOf(sampleEpisodes))

        viewModel = EpisodeViewModel(getEpisodes, searchEpisodes)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is EpisodeUiState.Success)
        assertEquals(2, (state as EpisodeUiState.Success).episodes.size)
        verify(getEpisodes).invoke()
    }

    @Test
    fun onQueryChanged_callsSearchAndUpdatesState() = runTest {
        whenever(getEpisodes()).thenReturn(flowOf(sampleEpisodes))
        whenever(searchEpisodes("test")).thenReturn(flowOf(listOf(sampleEpisodes.first())))

        viewModel = EpisodeViewModel(getEpisodes, searchEpisodes)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onQueryChanged("test")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as EpisodeUiState.Success
        assertEquals("test", state.query)
        assertEquals(1, state.episodes.size)
        verify(searchEpisodes).invoke("test")
    }
}