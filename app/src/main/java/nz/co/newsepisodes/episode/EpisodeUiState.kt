package nz.co.newsepisodes.episode

import nz.co.newsepisodes.common.model.Episode

sealed interface EpisodeUiState {
    data object Loading : EpisodeUiState

    data class Success(
        val episodes: List<Episode>,
        val query: String = ""
    ) : EpisodeUiState

    data class Error(
        val message: String
    ) : EpisodeUiState
}