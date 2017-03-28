package site.paulo.localchat.data.remote

import retrofit2.http.GET
import rx.Observable
import site.paulo.localchat.data.model.Ribot

interface RibotsService {
    @GET("ribots")
    fun getRibots(): Observable<List<Ribot>>
}
