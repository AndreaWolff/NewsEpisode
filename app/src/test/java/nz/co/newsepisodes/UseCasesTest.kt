package nz.co.newsepisodes

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import nz.co.newsepisodes.common.usecase.GetEpisodeUseCase
import nz.co.newsepisodes.common.usecase.GetEpisodeUseCaseImpl
import nz.co.newsepisodes.common.usecase.GetEpisodesUseCase
import nz.co.newsepisodes.common.usecase.GetEpisodesUseCaseImpl
import nz.co.newsepisodes.common.usecase.SearchEpisodesUseCase
import nz.co.newsepisodes.common.usecase.SearchEpisodesUseCaseImpl
import nz.co.newsepisodes.common.usecase.ToggleSavedUseCase
import nz.co.newsepisodes.common.usecase.ToggleSavedUseCaseImpl
import nz.co.newsepisodes.common.repo.EpisodeRepository
import nz.co.newsepisodes.common.model.Episode
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UseCasesTest {
    @Mock
    private lateinit var repository: EpisodeRepository

    private lateinit var getEpisodes: GetEpisodesUseCase
    private lateinit var searchEpisodes: SearchEpisodesUseCase
    private lateinit var getEpisode: GetEpisodeUseCase
    private lateinit var toggleSaved: ToggleSavedUseCase

    private val sampleEpisode = Episode("1", "Test", "Show", "Desc", 10, "2026-01-01")

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getEpisodes = GetEpisodesUseCaseImpl(repository)
        searchEpisodes = SearchEpisodesUseCaseImpl(repository)
        getEpisode = GetEpisodeUseCaseImpl(repository)
        toggleSaved = ToggleSavedUseCaseImpl(repository)
    }

    @Test
    fun getEpisodesUseCase_delegatesToRepository() = runTest {
        whenever(repository.getEpisodes()).thenReturn(flowOf(listOf(sampleEpisode)))

        val result = getEpisodes().first()

        assertEquals(1, result.size)
        assertEquals("Test", result.first().title)
        verify(repository).getEpisodes()
    }

    @Test
    fun searchEpisodesUseCase_delegatesToRepository() = runTest {
        whenever(repository.search("query")).thenReturn(listOf(sampleEpisode))

        val result = searchEpisodes("query")

        assertEquals(1, result.size)
        assertEquals("Test", result.first().title)
        verify(repository).search("query")
    }

    @Test
    fun getEpisodeUseCase_delegatesToRepository() = runTest {
        whenever(repository.getEpisode("1")).thenReturn(sampleEpisode)

        val result = getEpisode("1")

        assertEquals("Test", result?.title)
        verify(repository).getEpisode("1")
    }

    @Test
    fun toggleSavedUseCase_delegatesToRepository() = runTest {
        toggleSaved("1")
        verify(repository).toggleSaved("1")
    }
}