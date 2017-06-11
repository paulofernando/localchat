package site.paulo.localchat.data.remote

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kelvinapps.rxfirebase.DataSnapshotMapper
import com.kelvinapps.rxfirebase.RxFirebaseDatabase
import rx.Observable
import site.paulo.localchat.data.model.chatgeo.Chat
import site.paulo.localchat.data.model.chatgeo.User
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.Typography.registered

@Singleton
class FirebaseHelper @Inject constructor(val firebaseDatabase: FirebaseDatabase) {

    val CHILD_CHATS = "chats"
    val CHILD_USERS = "users"

    fun getUsers(): Observable<List<User>> {
        return RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.getReference(CHILD_USERS),
            DataSnapshotMapper.listOf(User::class.java))
    }

    fun registerUser(user:User): Unit {
        val value = mutableMapOf<String, Any>()
        value.put("email", user.email)
        value.put("name", user.name)
        value.put("age", user.age)
        value.put("gender", user.gender)

        val completionListener = DatabaseReference.CompletionListener { databaseError, databaseReference ->
            println("User " + user.email + "registered")
        }
        firebaseDatabase.getReference(CHILD_USERS).push().setValue(value, completionListener)


    }

    fun getChatRooms(chatId:String): Observable<Chat> {
        return RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.getReference(CHILD_CHATS).child(chatId),
            Chat::class.java)
    }

}
