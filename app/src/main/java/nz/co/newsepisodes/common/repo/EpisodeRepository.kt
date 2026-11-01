package nz.co.newsepisodes.common.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import nz.co.newsepisodes.common.di.CoroutineDispatchers
import nz.co.newsepisodes.common.model.Episode

interface EpisodeRepository {
    fun getEpisodes(): Flow<List<Episode>>
    suspend fun search(query: String): List<Episode>
    suspend fun getEpisode(id: String): Episode?
    suspend fun toggleSaved(id: String)
}

class EpisodeRepositoryImpl(
    private val dispatchers: CoroutineDispatchers,
) : EpisodeRepository {

    private val allEpisodes = MutableStateFlow(
        listOf(
            Episode(
                "1",
                "Morning Briefing",
                "News",
                "Today's top Australian stories",
                12,
                "2026-10-30"
            ),
            Episode(
                "2",
                "Politics Now",
                "News",
                "Analysis of the latest policy debates",
                28,
                "2026-10-29"
            ),
            Episode(
                "3",
                "Science Hour",
                "Radio National",
                "New research on climate adaptation",
                45,
                "2026-10-28"
            ),
            Episode(
                "4",
                "Kids Corner",
                "Kids Listen",
                "Stories and songs for young listeners",
                15,
                "2026-10-27"
            ),
            Episode(
                "5",
                "Hottest 100 Countdown",
                "Hot 100",
                "Looking back at the biggest tracks",
                60,
                "2026-10-26"
            ),
            Episode("6", "Sport Wrap", "Sport", "AFL and NRL round-up", 22, "2026-10-25"),
            Episode(
                "7",
                "Culture Club",
                "Arts",
                "Interviews with Australian creators",
                35,
                "2026-10-24"
            ),
            Episode(
                "8",
                "Drive Home",
                "Radio",
                "Afternoon conversation and music",
                40,
                "2026-10-23"
            )
        )
    )

    override fun getEpisodes(): Flow<List<Episode>> = allEpisodes.map { list -> list }

    override suspend fun search(query: String): List<Episode> = withContext(dispatchers.io) {
        val value = query.trim().lowercase()
        val list = allEpisodes.value
        if (value.isEmpty()) {
            list
        } else {
            list.filter {
                it.title.lowercase().contains(value) ||
                        it.showName.lowercase().contains(value) ||
                        it.description.lowercase().contains(value)
            }
        }
    }

    override suspend fun getEpisode(id: String): Episode? = withContext(dispatchers.io) {
        allEpisodes.value.find { it.id == id }
    }

    override suspend fun toggleSaved(id: String) = withContext(dispatchers.io) {
        allEpisodes.update { list ->
            list.map { episode ->
                if (episode.id == id) episode.copy(isSaved = !episode.isSaved) else episode
            }
        }
    }
}