package nz.co.newsepisodes.app

import android.app.Application
import nz.co.newsepisodes.common.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class EpisodeApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@EpisodeApplication)
            modules(appModule)
        }
    }
}