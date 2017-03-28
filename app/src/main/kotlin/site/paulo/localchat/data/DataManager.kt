package site.paulo.localchat.data

import javax.inject.Inject
import javax.inject.Singleton

import rx.Observable
import site.paulo.localchat.data.local.DatabaseHelper
import site.paulo.localchat.data.model.ForecastList
import site.paulo.localchat.data.model.Ribot
import site.paulo.localchat.data.remote.ForecastsService
import site.paulo.localchat.data.remote.RibotsService

@Singleton
class DataManager @Inject constructor(private val ribotsService: RibotsService,
                                      private val forecastService: ForecastsService,
                                      private val databaseHelper: DatabaseHelper) {

    fun syncRibots(): Observable<Ribot> {
        return ribotsService.getRibots()
                .concatMap { databaseHelper.setRibots(it) }
    }

    fun getRibots(): Observable<List<Ribot>> {
        return databaseHelper.getRibots().distinct()
    }

    fun getForecasts(): Observable<ForecastList> {
        return forecastService.getForecasts();
    }
}
