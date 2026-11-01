package nz.co.newsepisodes.episode

import nz.co.newsepisodes.common.model.Episode

data class EpisodeUiState(
    val query: String = "",
    val status: Status = Status.Loading
) {
    sealed interface Status {
        object Loading: Status
        data class Success(val episodes: List<Episode>): Status
        data class Error(val message: String): Status
    }
}