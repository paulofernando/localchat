package site.paulo.localchat.injection.component

import android.app.Application
import android.content.Context
import dagger.Component
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.SyncService
import site.paulo.localchat.data.local.DatabaseHelper
import site.paulo.localchat.data.remote.ForecastsService
import site.paulo.localchat.data.remote.RibotsService
import site.paulo.localchat.injection.ApplicationContext
import site.paulo.localchat.injection.module.ApplicationModule
import site.paulo.localchat.injection.module.DataModule

import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, DataModule::class))
interface ApplicationComponent {
    fun inject(syncService: SyncService)

    @ApplicationContext fun context(): Context
    fun application(): Application
    fun ribotsService(): RibotsService
    fun forecastsService(): ForecastsService
    fun databaseHelper(): DatabaseHelper
    fun dataManager(): DataManager
}
