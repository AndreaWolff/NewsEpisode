package nz.co.newsepisodes.episode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import nz.co.newsepisodes.common.usecase.GetEpisodesUseCase
import nz.co.newsepisodes.common.usecase.SearchEpisodesUseCase

class EpisodeViewModel(
    private val getEpisodes: GetEpisodesUseCase,
    private val searchEpisodes: SearchEpisodesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EpisodeUiState>(EpisodeUiState.Loading)
    val uiState: StateFlow<EpisodeUiState> = _uiState.asStateFlow()

    private var currentQuery: String = ""

    init {
        loadEpisodes()
    }

    fun loadEpisodes() {
        viewModelScope.launch {
            _uiState.value = EpisodeUiState.Loading
            getEpisodes()
                .catch { e ->
                    _uiState.value = EpisodeUiState.Error(
                        e.message ?: "Something went wrong!"
                    )
                }
                .collect { episodes ->
                    _uiState.value = EpisodeUiState.Success(
                       episodes = episodes,
                        query = currentQuery
                    )
                }
        }
    }

    fun onQueryChanged(query: String) {
        currentQuery = query
        viewModelScope.launch {
            val previous = (_uiState.value as? EpisodeUiState.Success)
            _uiState.value = EpisodeUiState.Success(
                episodes = previous?.episodes ?: emptyList(),
                query = query
            )

            searchEpisodes(query)
                .catch { e ->
                    _uiState.value = EpisodeUiState.Error(
                        e.message ?: "Search Failed"
                    )
                }
                .collect { results ->
                    _uiState.value = EpisodeUiState.Success(
                        episodes = results,
                        query = query
                    )
                }
        }
    }

    fun retry() {
        when {
            currentQuery.isBlank() -> loadEpisodes()
            else -> onQueryChanged(currentQuery)
        }
    }
}