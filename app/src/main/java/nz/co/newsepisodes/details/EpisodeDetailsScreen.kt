package nz.co.newsepisodes.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import nz.co.newsepisodes.R
import nz.co.newsepisodes.common.model.Episode
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parameterSetOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeDetailsScreen(
    episodeId: String,
    onBack: () -> Unit,
    viewModel: EpisodeDetailsViewModel = koinViewModel(parameters = { parameterSetOf(episodeId) })
) {
    val uiState: EpisodeDetailUiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Episode") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = viewModel::onSavedClicked,
                        modifier = Modifier.testTag("save_button")
                    ) {
                        val isSaved = when (val content = uiState.status) {
                            is EpisodeDetailUiState.DetailStatus.Success -> content.episode.isSaved
                            else -> false
                        }
                        Icon(
                            painter = if (isSaved) painterResource(R.drawable.baseline_favorite_24) else painterResource(
                                R.drawable.outline_favorite_24
                            ),
                            contentDescription = if (isSaved) "Unsave" else "Save",
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Spacer(Modifier.height(16.dp))

            when (val state = uiState.status) {
                is EpisodeDetailUiState.DetailStatus.Error -> Text(
                    state.message,
                    style = MaterialTheme.typography.bodyLarge
                )
                is EpisodeDetailUiState.DetailStatus.Loading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                is EpisodeDetailUiState.DetailStatus.Success -> EpisodeDetailContent(
                    state.episode
                )
            }
        }
    }
}

@Composable
private fun EpisodeDetailContent(
    episode: Episode
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            episode.title,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            episode.showName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            "${episode.durationMinutes} min • ${episode.publishedAt}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            episode.description,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}