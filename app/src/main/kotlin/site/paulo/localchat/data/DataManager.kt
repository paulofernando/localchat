/*
 * Copyright 2017 Paulo Fernando
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package site.paulo.localchat.data

import com.google.firebase.auth.AuthResult
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.kelvinapps.rxfirebase.RxFirebaseChildEvent
import com.kelvinapps.rxfirebase.RxFirebaseDatabase
import rx.Observable
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.data.model.firebase.User
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

    fun updateToken(token: String?): Unit {
        firebaseHelper.updateToken(token)
    }

    fun updateProfilePic(url: String?): Unit {
        firebaseHelper.updateToken(url)
    }

    fun authenticateUser(email: String, password: String): Observable<AuthResult> {
        return firebaseHelper.authenticateUser(email, password)
    }

    fun updateUserData(dataType: FirebaseHelper.Companion.UserDataType,
        value:String, completionListener: DatabaseReference.CompletionListener): Unit {
        firebaseHelper.updateUserData(dataType, value, completionListener)
    }

    fun getChatRoom(chatId:String): Observable<Chat> {
        return firebaseHelper.getChatRoom(chatId)
    }

    fun createNewRoom(otherUser: User): Chat {
        return firebaseHelper.createNewRoom(otherUser)
    }

    fun sendMessage(message: ChatMessage, chatId: String, completionListener: DatabaseReference.CompletionListener): Unit {
        return firebaseHelper.sendMessage(message, chatId, completionListener)
    }

    fun registerUserChildEventListener(listener: ChildEventListener): Unit {
        firebaseHelper.registerUserChildEventListener(listener)
    }

    fun registerUserValueEventListener(listener: ValueEventListener): Unit {
        firebaseHelper.registerUserValueEventListener(listener)
    }

    fun registerRoomChildEventListener(listener: ChildEventListener, roomId: String): Boolean {
        return firebaseHelper.registerRoomChildEventListener(listener, roomId)
    }

    fun registerRoomValueEventListener(listener: ValueEventListener, roomId: String): Boolean {
        return firebaseHelper.registerRoomValueEventListener(listener, roomId)
    }

    fun registerNewChatRoomChildEventListener(listener: ChildEventListener, userId: String? = null): Unit {
        firebaseHelper.registerNewChatRoomChildEventListener(listener, userId)
    }

    fun messageDelivered(roomId: String): Unit {
        firebaseHelper.messageDelivered(roomId)
    }

    fun removeAllListeners(): Unit {
        firebaseHelper.removeAllListeners()
    }


}
