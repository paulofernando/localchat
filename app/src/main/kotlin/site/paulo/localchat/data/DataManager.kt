package site.paulo.localchat.data

import rx.Observable
import site.paulo.localchat.data.local.DatabaseHelper
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.data.model.ribot.Ribot
import site.paulo.localchat.data.remote.ChatGeoService
import site.paulo.localchat.data.remote.RibotsService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataManager
@Inject constructor(private val ribotsService: RibotsService,
    private val chatGeoService: ChatGeoService,
    private val databaseHelper: DatabaseHelper) {

    fun syncRibots(): Observable<Ribot> {
        return ribotsService.getRibots()
            .concatMap { databaseHelper.setRibots(it) }
    }

    fun getRibots(): Observable<List<Ribot>> {
        return databaseHelper.getRibots().distinct()
    }

    fun getUsers(): Observable<List<User>> {
        return chatGeoService.getUsers()
    }
}
