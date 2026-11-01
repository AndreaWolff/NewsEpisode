package nz.co.newsepisodes.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import nz.co.newsepisodes.common.usecase.GetEpisodeUseCase
import nz.co.newsepisodes.common.usecase.ToggleSavedUseCase

class EpisodeDetailsViewModel(
    private val episodeId: String,
    private val getEpisode: GetEpisodeUseCase,
    private val toggledSaved: ToggleSavedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EpisodeDetailUiState>(EpisodeDetailUiState.Loading)
    val uiState: StateFlow<EpisodeDetailUiState> = _uiState.asStateFlow()

    init {
        loadEpisode()
    }

    private fun loadEpisode() {
        viewModelScope.launch {
            getEpisode(episodeId)
                .catch { e ->
                    _uiState.value = EpisodeDetailUiState.Error(e.message ?: "Failed to load episode")
                }
                .collect { episode ->
                    _uiState.value = if (episode != null) {
                        EpisodeDetailUiState.Success(episode)
                    } else {
                        EpisodeDetailUiState.Error("Episode not found")
                    }
                }
        }
    }

    fun onSavedClicked() {
        viewModelScope.launch {
            toggledSaved(episodeId)
        }
    }
}