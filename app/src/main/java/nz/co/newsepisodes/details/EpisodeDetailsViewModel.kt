package nz.co.newsepisodes.details

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nz.co.newsepisodes.common.di.CoroutineDispatchers
import nz.co.newsepisodes.common.di.dispatcherScope
import nz.co.newsepisodes.common.usecase.GetEpisodeUseCase
import nz.co.newsepisodes.common.usecase.ToggleSavedUseCase

class EpisodeDetailsViewModel(
    private val episodeId: String,
    private val getEpisode: GetEpisodeUseCase,
    private val toggledSaved: ToggleSavedUseCase,
    dispatchers: CoroutineDispatchers,
) : ViewModel() {

    private val dispatcherScope = dispatcherScope(dispatchers)

    private val _uiState = MutableStateFlow((EpisodeDetailUiState()))
    val uiState: StateFlow<EpisodeDetailUiState> = _uiState.asStateFlow()

    init {
        loadEpisode()
    }

    private fun loadEpisode() {
        dispatcherScope.launch {
            _uiState.update { it.copy(status = EpisodeDetailUiState.DetailStatus.Loading) }

            runCatching { getEpisode(episodeId) }
                .onSuccess { episode ->
                    _uiState.update {
                        it.copy(
                            status = if (episode != null) {
                                EpisodeDetailUiState.DetailStatus.Success(episode)
                            } else {
                                EpisodeDetailUiState.DetailStatus.Error("Episode not found")
                            }
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            status = EpisodeDetailUiState.DetailStatus.Error(
                                e.message ?: "Something went wrong"
                            )
                        )
                    }
                }
        }
    }

    fun onSavedClicked() {
        dispatcherScope.launch {
            toggledSaved(episodeId)
            loadEpisode()
        }
    }
}