package site.paulo.localchat.data

import rx.Observable
import site.paulo.localchat.data.model.chatgeo.Chat
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

    fun registerUser(user: User): Unit {
        firebaseHelper.registerUser(user)
    }

    fun getChatRooms(chatId:String): Observable<Chat> {
        return firebaseHelper.getChatRooms(chatId)
    }
}
