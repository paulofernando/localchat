package site.paulo.localchat.data

import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

import rx.Observable
import site.paulo.localchat.data.local.DatabaseHelper
import site.paulo.localchat.data.model.forecast.ForecastList
import site.paulo.localchat.data.model.forecast.WeatherData
import site.paulo.localchat.data.model.place.Place
import site.paulo.localchat.data.model.ribot.Ribot
import site.paulo.localchat.data.remote.ForecastsService
import site.paulo.localchat.data.remote.PlaceService
import site.paulo.localchat.data.remote.RibotsService

@Singleton
class DataManager
@Inject constructor(private val ribotsService: RibotsService,
                    private val forecastService: ForecastsService,
                    private val placeService: PlaceService,
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

    fun getPlaces(): Observable<Map<String, Place>> {
        return placeService.getPlaces();
    }

    fun getWeatherInfo(latitude: String,
                       longitude: String,
                       cnt: String,
                       appid: String): Observable<WeatherData> {
        return forecastService.getWeatherInfo(latitude, longitude, cnt, appid);
    }
}
