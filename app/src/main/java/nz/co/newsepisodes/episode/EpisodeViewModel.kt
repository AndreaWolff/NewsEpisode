package nz.co.newsepisodes.episode

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nz.co.newsepisodes.common.di.CoroutineDispatchers
import nz.co.newsepisodes.common.di.dispatcherScope
import nz.co.newsepisodes.common.usecase.GetEpisodesUseCase
import nz.co.newsepisodes.common.usecase.SearchEpisodesUseCase
import kotlin.time.Duration.Companion.milliseconds

class EpisodeViewModel(
    private val getEpisodes: GetEpisodesUseCase,
    private val searchEpisodes: SearchEpisodesUseCase,
    dispatchers: CoroutineDispatchers,
) : ViewModel() {

    private val dispatcherScope = dispatcherScope(dispatchers)

    private val _uiState = MutableStateFlow(EpisodeUiState())
    val uiState: StateFlow<EpisodeUiState> = _uiState.asStateFlow()

    init {
        loadEpisodes()
    }

    fun loadEpisodes() {
        dispatcherScope.launch {
            _uiState.update { it.copy(status = EpisodeUiState.Status.Loading) }

            getEpisodes()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            status = EpisodeUiState.Status.Error(e.message ?: "Something went wrong")
                        )
                    }
                }
                .collect { result ->
                    _uiState.update {
                        it.copy(
                            status = EpisodeUiState.Status.Success(result)
                        )
                    }
                }
        }
    }

    fun onQueryChanged(query: String) {
        dispatcherScope.launch {
            _uiState.update { it.copy(
                query = query,
                status = EpisodeUiState.Status.Loading
            ) }

            delay(300.milliseconds)
            runCatching { searchEpisodes(query) }
                .onSuccess { results ->
                    _uiState.update { it.copy(status = EpisodeUiState.Status.Success(results)) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(status = EpisodeUiState.Status.Error(e.message ?: "Search failed")) }
                }
        }
    }

    fun retry() {
        val query = _uiState.value.query
        if (query.isBlank()) loadEpisodes() else onQueryChanged(query)
    }

    fun clearSearch() {
        _uiState.update { it.copy(query = "") }
        loadEpisodes()
    }
}