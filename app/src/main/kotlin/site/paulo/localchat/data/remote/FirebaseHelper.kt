package site.paulo.localchat.data.remote

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.kelvinapps.rxfirebase.DataSnapshotMapper
import com.kelvinapps.rxfirebase.RxFirebaseAuth
import com.kelvinapps.rxfirebase.RxFirebaseDatabase
import rx.Observable
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.data.model.firebase.User
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseHelper @Inject constructor(val firebaseDatabase: FirebaseDatabase,
    val currentUserManager: CurrentUserManager,
    val firebaseAuth: FirebaseAuth) {

    object Child {
        val CHILD_CHATS = "chats"
        val CHILD_USERS = "users"
        val CHILD_MESSAGES = "messages"
        val CHILD_TIMESTAMP = "timestamp"
    }

    companion object {
        enum class UserDataType {
            NAME, AGE
        }
    }

    /**************** User *********************/

    fun getUsers(): Observable<List<User>> {
        return RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.getReference(Child.CHILD_USERS),
            DataSnapshotMapper.listOf(User::class.java))
    }

    fun getUser(userId:String): Observable<User> {
        return RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.getReference(Child.CHILD_USERS).child(userId),
            User::class.java)
    }

    fun registerUser(user:User): Unit {
        val value = mutableMapOf<String, Any>()
        value.put("email", user.email)
        value.put("name", user.name)
        value.put("age", user.age)
        value.put("gender", user.gender)

        val completionListener = DatabaseReference.CompletionListener { databaseError, databaseReference ->
            Timber.e("User " + user.email + "registered")
        }
        firebaseDatabase.getReference(Child.CHILD_USERS).push().setValue(value, completionListener)
    }

    fun authenticateUser(email: String, password: String): Observable<AuthResult> {
        return RxFirebaseAuth.signInWithEmailAndPassword(firebaseAuth, email, password)
    }

    fun updateUserData(dataType: UserDataType, value:String, completionListener: DatabaseReference.CompletionListener): Unit {
        when(dataType) {
            UserDataType.NAME -> {
                val v = mutableMapOf<String, Any>()
                v.put("name", value)
                firebaseDatabase.getReference(Child.CHILD_USERS)
                    .child(currentUserManager.getUserId())
                    .updateChildren(v, completionListener)
            }
            UserDataType.AGE -> {
                val v = mutableMapOf<String, Any>()
                v.put("age", value.toInt())
                firebaseDatabase.getReference(Child.CHILD_USERS)
                    .child(currentUserManager.getUserId()).updateChildren(v, completionListener)
            }
            else -> Timber.e("Invalid UserDataType")
        }
    }

    /**************** Chat *********************/

    fun sendMessage(message: ChatMessage, chatId: String, completionListener: DatabaseReference.CompletionListener): Unit {
        val valueMessage = mutableMapOf<String, Any>()
        valueMessage.put("owner", message.owner)
        valueMessage.put("message", message.message)
        valueMessage.put("timestamp", ServerValue.TIMESTAMP)

        val valueLastMessage = mutableMapOf<String, Any>()
        valueLastMessage.put("lastMessage", message.owner + ": " + message.message)

        firebaseDatabase.getReference(Child.CHILD_MESSAGES).child(chatId).push().setValue(valueMessage, completionListener)
        firebaseDatabase.getReference(Child.CHILD_CHATS).child(chatId).updateChildren(valueLastMessage)
    }

    fun getChatRoom(chatId:String): Observable<Chat> {
        return RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.getReference(Child.CHILD_CHATS).child(chatId),
            Chat::class.java)
    }


}
