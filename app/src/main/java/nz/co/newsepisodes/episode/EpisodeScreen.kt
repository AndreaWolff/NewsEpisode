package nz.co.newsepisodes.episode

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import nz.co.newsepisodes.R
import nz.co.newsepisodes.common.model.Episode
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodesScreen(
    onEpisodeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EpisodeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentQuery = (uiState as? EpisodeUiState.Success)?.query ?: ""

    val textFieldState = rememberTextFieldState(currentQuery)
    val searchBarState = rememberSearchBarState()

    LaunchedEffect(currentQuery) {
        if (textFieldState.text.toString() != currentQuery) {
            textFieldState.edit { replace(0, length, currentQuery) }
        }
    }

    LaunchedEffect(textFieldState.text) {
        val newQuery = textFieldState.text.toString()
        if (newQuery != currentQuery) {
            viewModel.onQueryChanged(newQuery)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Listen",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics {
                contentDescription = "Listen episode"
            }
        )

        Spacer(Modifier.height(12.dp))

        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            state = searchBarState,
            inputField = {
                SearchBarDefaults.InputField(
                    textFieldState = textFieldState,
                    searchBarState = searchBarState,
                    onSearch = { viewModel.onQueryChanged(textFieldState.text.toString()) },
                    placeholder = { Text("Search episodes") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.outline_search_24),
                            contentDescription = "Search"
                        )
                    },
                    modifier = Modifier
                        .testTag("search_field")
                        .semantics { contentDescription = "Search episode field" }
                )
            }
        )

        Spacer(Modifier.height(16.dp))

        when (val state = uiState) {
            is EpisodeUiState.Loading -> LoadingContent()
            is EpisodeUiState.Error -> ErrorContent(
                message = state.message,
                onRetry = viewModel::retry
            )

            is EpisodeUiState.Success -> {
                when {
                    state.episodes.isEmpty() -> EmptyContent(query = state.query)
                    else -> EpisodeList(
                        episodes = state.episodes,
                        onEpisodeClick = onEpisodeClick
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.semantics {
                contentDescription = "Loading episodes"
            }
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.semantics {
                contentDescription = "Error"
            }
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun EmptyContent(query: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (query.isBlank()) "No episodes available" else "No results for \"query\"",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun EpisodeList(
    episodes: List<Episode>,
    onEpisodeClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = episodes,
            key = { it.id }
        ) { episode ->
            EpisodeCard(
                episode,
                onClick = { onEpisodeClick(episode.id) }
            )
        }
    }
}

@Composable
private fun EpisodeCard(episode: Episode, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
            .semantics(mergeDescendants = true) {
                contentDescription =
                    "${episode.title}, ${episode.showName}, ${episode.durationMinutes}"
            }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = episode.title,
                style = MaterialTheme.typography.titleMedium
            )

            Row {
                Text(
                    text = episode.showName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = episode.publishedAt,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(4.dp))

            Row {
                Text(
                    text = episode.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )

                Spacer(Modifier.weight(1f))

                if (episode.isSaved) {
                    Spacer(Modifier.padding(start = 8.dp))
                    Text(
                        text = "Saved",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
