package site.paulo.localchat.data

import com.google.firebase.database.DatabaseReference
import rx.Observable
import site.paulo.localchat.data.model.chatgeo.Chat
import site.paulo.localchat.data.model.chatgeo.ChatMessage
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.data.remote.FirebaseHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataManager
@Inject constructor(private val firebaseHelper: FirebaseHelper) {

    fun getUsers(): Observable<List<User>> {
        return firebaseHelper.getUsers()
    }

    fun getUser(userId: String): Observable<User> {
        return firebaseHelper.getUser(userId)
    }

    fun registerUser(user: User): Unit {
        firebaseHelper.registerUser(user)
    }

    fun updateUserData(dataType: FirebaseHelper.Companion.UserDataType,
        value:String, completionListener: DatabaseReference.CompletionListener): Unit {
        firebaseHelper.updateUserData(dataType, value, completionListener)
    }

    fun getChatRoom(chatId:String): Observable<Chat> {
        return firebaseHelper.getChatRoom(chatId)
    }

    fun sendMessage(message: ChatMessage, chatId: String, completionListener: DatabaseReference.CompletionListener): Unit {
        return firebaseHelper.sendMessage(message, chatId, completionListener)
    }

}
