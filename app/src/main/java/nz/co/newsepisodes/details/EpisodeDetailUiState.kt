package nz.co.newsepisodes.details

import nz.co.newsepisodes.common.model.Episode

data class EpisodeDetailUiState(
    val status: DetailStatus = DetailStatus.Loading
) {
    sealed interface DetailStatus {
        object Loading: DetailStatus
        data class Success(val episode: Episode): DetailStatus
        data class Error(val message: String): DetailStatus
    }
}
