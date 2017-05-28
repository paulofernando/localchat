package site.paulo.localchat.data

import com.google.firebase.database.FirebaseDatabase
import com.kelvinapps.rxfirebase.DataSnapshotMapper
import com.kelvinapps.rxfirebase.RxFirebaseDatabase
import rx.Observable
import site.paulo.localchat.data.local.DatabaseHelper
import site.paulo.localchat.data.model.chatgeo.ChatMessage
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
    private val databaseHelper: DatabaseHelper,
    private val firebaseDatabase: FirebaseDatabase) {

    fun syncRibots(): Observable<Ribot> {
        return ribotsService.getRibots()
            .concatMap { databaseHelper.setRibots(it) }
    }

    fun getRibots(): Observable<List<Ribot>> {
        return databaseHelper.getRibots().distinct()
    }

    fun getUsers(): Observable<List<User>> {
        //return chatGeoService.getUsers()
        return RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.getReference("users"),
            DataSnapshotMapper.listOf(User::class.java));
    }
}
