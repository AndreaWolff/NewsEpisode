package nz.co.newsepisodes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import nz.co.newsepisodes.details.EpisodeDetailsScreen
import nz.co.newsepisodes.episode.EpisodesScreen

sealed class Screen(val route: String) {
    data object List: Screen("episodes")
    data object Detail: Screen("episode/{episodeId}") {
        fun createRoute(episodeId: String) = "episode/$episodeId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.List.route
    ) {
        composable(Screen.List.route) {
            EpisodesScreen(
                onEpisodeClick = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
                }
            )
        }
        composable(Screen.Detail.route) { backStackEntry ->
            val episodeId = backStackEntry.arguments?.getString("episodeId") ?: return@composable

            EpisodeDetailsScreen(
                episodeId = episodeId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}