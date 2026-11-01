package nz.co.newsepisodes

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import nz.co.newsepisodes.common.di.CoroutineDispatchers
import nz.co.newsepisodes.common.repo.EpisodeRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EpisodeRepositoryImplTest {
    private val testDispatcher = StandardTestDispatcher()

    private val dispatchers = object : CoroutineDispatchers {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
    }

    private val repository = EpisodeRepositoryImpl(dispatchers)

    @Test
    fun getEpisodes_returnsAllEpisodes() = runTest(testDispatcher) {
        val result = repository.getEpisodes().first()
        assertEquals(8, result.size)
        assertEquals("Morning Briefing", result.first().title)
    }

    @Test
    fun search_filtersByTitle() = runTest(testDispatcher) {
        val result = repository.search("Politics")
        assertEquals(1, result.size)
        assertEquals("Politics Now", result.first().title)
    }

    @Test
    fun search_emptyQuery_returnsAll() = runTest(testDispatcher) {
        val result = repository.search("")
        assertEquals(8, result.size)
    }

    @Test
    fun search_noMatch_returnsEmpty() = runTest(testDispatcher) {
        val result = repository.search("zzzzzzzz")
        assertTrue(result.isEmpty())
    }

    @Test
    fun getEpisode_existingId_returnsEpisode() = runTest(testDispatcher) {
        val result = repository.getEpisode("1")
        assertNotNull(result)
        assertEquals("Morning Briefing", result?.title)
    }

    @Test
    fun getEpisode_unknownId_returnsNull() = runTest(testDispatcher) {
        val result = repository.getEpisode("999")
        assertNull(result)
    }

    @Test
    fun toggleSaved_flipsIsSaved() = runTest(testDispatcher) {
        assertFalse(repository.getEpisode("1")?.isSaved == true)

        repository.toggleSaved("1")
        assertTrue(repository.getEpisode("1")?.isSaved == true)

        repository.toggleSaved("1")
        assertFalse(repository.getEpisode("1")?.isSaved == true)
    }
}