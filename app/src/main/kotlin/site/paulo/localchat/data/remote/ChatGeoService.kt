package site.paulo.localchat.data.remote

import retrofit2.http.GET
import rx.Observable
import site.paulo.localchat.data.model.firebase.User

interface ChatGeoService {

    @GET("users")
    fun getUsers(): Observable<List<User>>

}