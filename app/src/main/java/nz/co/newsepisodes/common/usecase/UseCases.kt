package nz.co.newsepisodes.common.usecase

import kotlinx.coroutines.flow.Flow
import nz.co.newsepisodes.common.model.Episode
import nz.co.newsepisodes.common.repo.EpisodeRepository

interface GetEpisodesUseCase {
    operator fun invoke(): Flow<List<Episode>>
}

interface SearchEpisodesUseCase {
    operator fun invoke(query: String): Flow<List<Episode>>
}

interface GetEpisodeUseCase {
    operator fun invoke(id: String): Flow<Episode?>
}

interface ToggleSavedUseCase {
    suspend operator fun invoke(id: String)
}

class GetEpisodesUseCaseImpl(
    private val repository: EpisodeRepository
) : GetEpisodesUseCase {
    override fun invoke(): Flow<List<Episode>> = repository.getEpisodes()
}

class SearchEpisodesUseCaseImpl(
    private val repository: EpisodeRepository
) : SearchEpisodesUseCase {
    override fun invoke(query: String): Flow<List<Episode>> = repository.search(query)
}

class GetEpisodeUseCaseImpl(
    private val repository: EpisodeRepository
) : GetEpisodeUseCase {
    override fun invoke(id: String): Flow<Episode?> = repository.getEpisode(id)
}

class ToggleSavedUseCaseImpl(
    private val repository: EpisodeRepository
) : ToggleSavedUseCase {
    override suspend fun invoke(id: String) = repository.toggleSaved(id)
}