package nz.co.newsepisodes.common.di

import nz.co.newsepisodes.common.repo.EpisodeRepository
import nz.co.newsepisodes.common.repo.EpisodeRepositoryImpl
import nz.co.newsepisodes.common.usecase.GetEpisodeUseCase
import nz.co.newsepisodes.common.usecase.GetEpisodeUseCaseImpl
import nz.co.newsepisodes.common.usecase.GetEpisodesUseCase
import nz.co.newsepisodes.common.usecase.GetEpisodesUseCaseImpl
import nz.co.newsepisodes.common.usecase.SearchEpisodesUseCase
import nz.co.newsepisodes.common.usecase.SearchEpisodesUseCaseImpl
import nz.co.newsepisodes.common.usecase.ToggleSavedUseCase
import nz.co.newsepisodes.common.usecase.ToggleSavedUseCaseImpl
import nz.co.newsepisodes.details.EpisodeDetailsViewModel
import nz.co.newsepisodes.episode.EpisodeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<CoroutineDispatchers> { CoroutineDispatchersImpl() }

    single<EpisodeRepository> { EpisodeRepositoryImpl() }

    factory<GetEpisodesUseCase> { GetEpisodesUseCaseImpl(get()) }
    factory<SearchEpisodesUseCase> { SearchEpisodesUseCaseImpl(get()) }
    factory<GetEpisodeUseCase> { GetEpisodeUseCaseImpl(get()) }
    factory<ToggleSavedUseCase> { ToggleSavedUseCaseImpl(get()) }

    viewModel { EpisodeViewModel(get(), get(), get()) }
    viewModel { params ->
        EpisodeDetailsViewModel(params.get(), get(), get(), get())
    }
}