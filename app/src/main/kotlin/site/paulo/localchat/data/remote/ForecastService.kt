package site.paulo.localchat.data.remote

import retrofit2.http.GET
import rx.Observable
import site.paulo.localchat.data.model.ForecastList

interface ForecastsService {

    @GET("forecast/daily?mode=json&units=metric&cnt=7&APPID=15646a06818f61f7b8d7823ca833e1ce&q=94043")
    fun getForecasts(): Observable<ForecastList>
}
