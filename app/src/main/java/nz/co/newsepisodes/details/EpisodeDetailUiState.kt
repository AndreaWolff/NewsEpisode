package nz.co.newsepisodes.details

import nz.co.newsepisodes.common.model.Episode

sealed interface EpisodeDetailUiState {
    data object Loading: EpisodeDetailUiState
    data class Success(val episode: Episode): EpisodeDetailUiState
    data class Error(val message: String): EpisodeDetailUiState
}
