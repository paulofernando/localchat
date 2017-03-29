package site.paulo.localchat.data.remote

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import rx.Observable
import site.paulo.localchat.data.model.place.Place

interface PlaceService {

    @GET("/places")
    fun getPlaces(): Observable<Map<String, Place>>

    @POST("/places/{id_place}/vote/{user}")
    fun voteForPlace(@Path("id_place") idPlace: String, @Path("user") user: String): Observable<Void>

    @GET("/places/placeofday")
    fun chosenPlaceToday(): Observable<Place>
}