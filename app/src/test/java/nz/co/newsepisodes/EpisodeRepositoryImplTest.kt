package nz.co.newsepisodes

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import nz.co.newsepisodes.common.model.Episode
import nz.co.newsepisodes.common.repo.EpisodeRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class EpisodeRepositoryImplTest {
    @Mock
    private lateinit var repository: EpisodeRepositoryImpl

    private val sampleEpisode = Episode(
        "1",
        "Morning Briefing",
        "News",
        "Today's top Australian stories",
        12,
        "2026-10-30"
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun getEpisodes_returnsExpectedList() = runTest {
        whenever(repository.getEpisodes()).thenReturn(flowOf(listOf(sampleEpisode)))

        val result = repository.getEpisodes().first()

        assertEquals(1, result.size)
        assertEquals("Morning Briefing", result.first().title)
        verify(repository).getEpisodes()
    }

    @Test
    fun search_returnsFilteredResults() = runTest {
        whenever(repository.search("Politics")).thenReturn(listOf(sampleEpisode))

        val result = repository.search("Politics")

        assertEquals(1, result.size)
        verify(repository).search("Politics")
    }

    @Test
    fun search_emptyQuery_returnsAll() = runTest {
        whenever(repository.search("")).thenReturn(listOf(sampleEpisode, sampleEpisode))

        val result = repository.search("")

        assertEquals(2, result.size)
        verify(repository).search("")
    }

    @Test
    fun getEpisode_existingId_returnsEpisode() = runTest {
        whenever(repository.getEpisode("1")).thenReturn(sampleEpisode)

        val result = repository.getEpisode("1")

        assertEquals("Morning Briefing", result?.title)
        verify(repository).getEpisode("1")
    }

    @Test
    fun getEpisode_unknownId_returnsNull() = runTest {
        whenever(repository.getEpisode("999")).thenReturn(null)

        val result = repository.getEpisode("999")

        assertNull(result)
        verify(repository).getEpisode("999")
    }

    @Test
    fun toggleSaved_isCalledWithCorrectId() = runTest {
        repository.toggleSaved("1")
        verify(repository).toggleSaved("1")
    }
}