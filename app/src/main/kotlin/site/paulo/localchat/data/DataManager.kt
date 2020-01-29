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

import android.location.Location
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.Maybe
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.data.model.firebase.NearbyUser
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.data.remote.FirebaseHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataManager
@Inject constructor(private val firebaseHelper: FirebaseHelper) {

    fun getUsers(): Maybe<List<User>> {
        return firebaseHelper.getUsers()
    }

    fun getNearbyUsers(geoHash: String): Maybe<List<Any>> {
        return firebaseHelper.getNearbyUsers(geoHash)
    }

    fun getUser(userId: String): Maybe<User> {
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

    fun updateUserLocation(location: Location?, callNext:(() -> Unit)? = null): Unit {
        firebaseHelper.updateUserLocation(location, callNext)
    }

    fun authenticateUser(email: String, password: String): Maybe<AuthResult> {
        return firebaseHelper.authenticateUser(email, password)
    }

    fun updateUserData(dataType: FirebaseHelper.Companion.UserDataType,
        value:String, completionListener: DatabaseReference.CompletionListener): Unit {
        firebaseHelper.updateUserData(dataType, value, completionListener)
    }

    fun getChatRoom(chatId:String): Maybe<Chat> {
        return firebaseHelper.getChatRoom(chatId)
    }

    fun createNewRoom(otherUser: NearbyUser): Chat {
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

    fun registerRoomChildEventListener(listener: ChildEventListener, roomId: String, listenerId: String? = null): Boolean {
        return firebaseHelper.registerRoomChildEventListener(listener, roomId, listenerId ?: roomId)
    }

    fun unregisterRoomChildEventListener(listener: ChildEventListener, roomId: String): Unit {
        return firebaseHelper.unregisterRoomChildEventListener(listener, roomId)
    }

    fun registerRoomValueEventListener(listener: ValueEventListener, roomId: String, listenerId: String? = null): Boolean {
        return firebaseHelper.registerRoomValueEventListener(listener, roomId, listenerId ?: roomId)
    }

    fun addRoomSingleValueEventListener(listener: ValueEventListener, roomId: String): Unit {
        firebaseHelper.addRoomSingleValueEventListener(listener, roomId)
    }

    fun unregisterRoomValueEventListener(listener: ValueEventListener, roomId: String): Unit {
        return firebaseHelper.unregisterRoomValueEventListener(listener, roomId)
    }

    fun registerNewChatRoomChildEventListener(listener: ChildEventListener, userId: String? = null): Unit {
        firebaseHelper.registerNewChatRoomChildEventListener(listener, userId)
    }

    fun registerNewUsersChildEventListener(listener: ChildEventListener): Unit {
        firebaseHelper.registerNewUsersChildEventListener(listener)
    }

    fun messageDelivered(roomId: String): Unit {
        firebaseHelper.messageDelivered(roomId)
    }

    fun removeAllListeners(): Unit {
        firebaseHelper.removeAllListeners()
    }


}
