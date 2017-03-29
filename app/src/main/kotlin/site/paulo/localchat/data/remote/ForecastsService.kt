package site.paulo.localchat.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable
import site.paulo.localchat.data.model.forecast.ForecastList
import site.paulo.localchat.data.model.forecast.WeatherData

interface ForecastsService {

    @GET("weather?q=London,uk&appid=b1b15e88fa797225412429c1c50c122a1")
    fun getForecasts(): Observable<ForecastList>

    @GET("/forecast")
    fun getWeatherInfo(@Query("lat") latitude: String,
                       @Query("lon") longitude: String,
                       @Query("cnt") cnt: String,
                       @Query("appid") appid: String): Observable<WeatherData>

}
