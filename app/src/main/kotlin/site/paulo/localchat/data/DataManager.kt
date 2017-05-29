package site.paulo.localchat.data

import com.google.firebase.database.FirebaseDatabase
import com.kelvinapps.rxfirebase.DataSnapshotMapper
import com.kelvinapps.rxfirebase.RxFirebaseDatabase
import rx.Observable
import site.paulo.localchat.data.model.chatgeo.Chat
import site.paulo.localchat.data.model.chatgeo.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataManager
@Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    val CHILD_CHATS = "chats"
    val CHILD_USERS = "users"

    fun getUsers(): Observable<List<User>> {
        return RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.getReference(CHILD_USERS),
            DataSnapshotMapper.listOf(User::class.java));
    }

    fun getChatRooms(chatId:String): Observable<Chat> {
        return RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.getReference(CHILD_CHATS).child(chatId),
            Chat::class.java);
    }
}
