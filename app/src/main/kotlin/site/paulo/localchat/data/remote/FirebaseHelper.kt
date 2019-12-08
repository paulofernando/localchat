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

package site.paulo.localchat.data.remote

import android.location.Location
import ch.hsr.geohash.GeoHash
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.kelvinapps.rxfirebase.DataSnapshotMapper
import com.kelvinapps.rxfirebase.RxFirebaseAuth
import com.kelvinapps.rxfirebase.RxFirebaseDatabase
import rx.Observable
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.ui.utils.Utils
import site.paulo.localchat.ui.utils.getFirebaseId
import timber.log.Timber
import java.util.HashMap
import javax.inject.Inject
import javax.inject.Singleton
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import site.paulo.localchat.data.model.firebase.*


@Singleton
class FirebaseHelper @Inject constructor(val firebaseDatabase: FirebaseDatabase,
                                         val currentUserManager: CurrentUserManager,
                                         val currentUser: CurrentUserManager,
                                         val firebaseAuth: FirebaseAuth) {

    object Reference {
        val CHATS = "chats"
        val MESSAGES = "messages"
        val USERS = "users"
        val GEOHASHES = "geohashes"
    }

    object Child {
        val ACCURACY = "acc"
        val AGE = "age"
        val CHATS = "chats"
        val DELIVERED_MESSAGES = "deliveredMessages"
        val EMAIL = "email"
        val GENDER = "gender"
        val GEOHASH = "geohash"
        val ID = "id"
        val LAST_MESSAGE = "lastMessage"
        val LAST_GEO_UPDATE = "lastGeoUpdate"
        val LATITUDE = "lat"
        val LOCATION = "loc"
        val LONGITUDE = "lon"
        val MESSAGE = "message"
        val NAME = "name"
        val OWNER = "owner"
        val PIC = "pic"
        val TIMESTAMP = "timestamp"
        val TOKEN = "token"
        val USERS = "users"
    }

    companion object {
        enum class UserDataType {
            NAME, AGE, PIC
        }
    }

    private var childEventListeners: HashMap<String, ChildEventListener> = HashMap()
    private var valueEventListeners: HashMap<String, ValueEventListener> = HashMap()

    /**************** User *********************/

    fun getUsers(): Observable<List<User>> {
        return RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.getReference(Reference.USERS),
                DataSnapshotMapper.listOf(User::class.java))
    }

    fun getNearbyUsers(geoHash: String): Observable<List<Object>> {
        return RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.getReference(Reference.GEOHASHES).child(geoHash),
                DataSnapshotMapper.listOf(Object::class.java))
    }

    fun getUser(userId: String): Observable<User> {
        return RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.getReference(Reference.USERS).child(userId),
                User::class.java)
    }

    fun registerUser(user: User): Unit {
        val userData = mutableMapOf<String, Any>()
        userData[Child.EMAIL] = user.email
        userData[Child.NAME] = user.name
        userData[Child.AGE] = user.age
        userData[Child.GENDER] = user.gender
        userData[Child.PIC] = "https://api.adorable.io/avatars/240/" + Utils.getFirebaseId(user.email) + ".png"

        val completionListener = DatabaseReference.CompletionListener { databaseError, databaseReference ->
            Timber.e("User %s registered", user.email)
        }

        firebaseDatabase.getReference(Reference.USERS).child(Utils.getFirebaseId(user.email)).setValue(userData, completionListener)
    }

    fun authenticateUser(email: String, password: String): Observable<AuthResult> {
        return RxFirebaseAuth.signInWithEmailAndPassword(firebaseAuth, email, password)
    }

    fun updateUserData(dataType: UserDataType, newValue: String, completionListener: DatabaseReference.CompletionListener): Unit {
        when (dataType) {
            UserDataType.NAME -> {
                val newName = mutableMapOf<String, Any>()
                newName[Child.NAME] = newValue
                firebaseDatabase.getReference(Reference.USERS)
                        .child(currentUserManager.getUserId())
                        .updateChildren(newName, completionListener)
            }
            UserDataType.AGE -> {
                val newAge = mutableMapOf<String, Any>()
                newAge[Child.AGE] = newValue.toInt()
                firebaseDatabase.getReference(Reference.USERS)
                        .child(currentUserManager.getUserId()).updateChildren(newAge, completionListener)
            }
            UserDataType.PIC -> {
                val newPic = mutableMapOf<String, Any>()
                newPic[Child.PIC] = newValue
                firebaseDatabase.getReference(Reference.USERS)
                        .child(currentUserManager.getUserId()).updateChildren(newPic, completionListener)
            }
            else -> Timber.e("Invalid UserDataType")
        }
    }

    fun updateToken(token: String?): Unit {
        val completionListener = DatabaseReference.CompletionListener { databaseError, databaseReference ->
            Timber.d("Token updated")
        }
        if (token != null) {
            val newToken = mutableMapOf<String, Any>()
            newToken[Child.TOKEN] = token
            firebaseDatabase.getReference(Reference.USERS)
                    .child(currentUserManager.getUserId())
                    .updateChildren(newToken, completionListener)
        }
    }

    fun updateProfilePic(url: String?): Unit {
        val completionListener = DatabaseReference.CompletionListener { databaseError, databaseReference ->
            Timber.d("Profile pic updated")
        }
        if (url != null) {
            val newProfilePic = mutableMapOf<String, Any>()
            newProfilePic[Child.PIC] = url
            firebaseDatabase.getReference(Reference.USERS)
                    .child(currentUserManager.getUserId())
                    .updateChildren(newProfilePic, completionListener)
        }
    }

    fun updateUserLocation(location: Location?, callNext: (() -> Unit)? = null): Unit {
        val locationCompletionListener = DatabaseReference.CompletionListener { databaseError, databaseReference ->
            Timber.d("Location updated")
        }

        val userInfoCompletionListener = DatabaseReference.CompletionListener { databaseError, databaseReference ->
            Timber.d("Geohash updated")
        }

        val removeListener = DatabaseReference.CompletionListener { databaseError, databaseReference ->
            Timber.d("Removed from old area")
        }

        if ((location != null) && (currentUserManager.getUserId() != null)) {
            val geoHash = GeoHash.geoHashStringWithCharacterPrecision(location.latitude, location.longitude, 5)
            currentUserManager.setGeohash(geoHash)
            val currentUser = currentUserManager.getUser()

            if (!geoHash.equals(currentUser.geohash))
                firebaseDatabase.getReference(Reference.GEOHASHES)
                        .child(currentUser.geohash)
                        .child(currentUserManager.getUserId())
                        .removeValue(removeListener)

            val newUserLocation = mutableMapOf<String, Any>()
            newUserLocation[Child.LATITUDE] = location.latitude
            newUserLocation[Child.LONGITUDE] = location.longitude
            newUserLocation[Child.ACCURACY] = location.accuracy
            newUserLocation[Child.GEOHASH] = geoHash
            firebaseDatabase.getReference(Reference.USERS)
                    .child(currentUserManager.getUserId())
                    .updateChildren(newUserLocation, locationCompletionListener)

            val newUserInfo = mutableMapOf<String, Any>()
            newUserInfo[Child.NAME] = currentUser.name
            newUserInfo[Child.PIC] = currentUser.pic
            newUserInfo[Child.EMAIL] = currentUser.email
            newUserInfo[Child.AGE] = currentUser.age
            newUserInfo[Child.LAST_GEO_UPDATE] = ServerValue.TIMESTAMP
            firebaseDatabase.getReference(Reference.GEOHASHES)
                    .child(geoHash)
                    .child(currentUserManager.getUserId())
                    .updateChildren(newUserInfo, userInfoCompletionListener)


            callNext?.invoke()
        }
    }

    fun registerUserChildEventListener(listener: ChildEventListener): Unit {
        registerChildEventListener(firebaseDatabase.getReference(FirebaseHelper.Reference.USERS)
                .child(currentUserManager.getUserId()), listener, "User")
    }

    fun registerUserValueEventListener(listener: ValueEventListener): Unit {
        registerValueEventListener(firebaseDatabase.getReference(FirebaseHelper.Reference.USERS)
                .child(currentUserManager.getUserId()), listener, "User")
    }

    /*******************************************/


    /**************** Room ****************/

    fun sendMessage(message: ChatMessage, chatId: String, completionListener: DatabaseReference.CompletionListener): Unit {
        val valueMessage = mutableMapOf<String, Any>()
        valueMessage[Child.OWNER] = message.owner
        valueMessage[Child.MESSAGE] = message.message
        valueMessage[Child.TIMESTAMP] = ServerValue.TIMESTAMP

        val valueLastMessage = mutableMapOf<String, Any>()
        valueLastMessage[Child.LAST_MESSAGE] = message

        firebaseDatabase.getReference(Reference.MESSAGES).child(chatId).push().setValue(valueMessage, completionListener)
        firebaseDatabase.getReference(Reference.CHATS).child(chatId).updateChildren(valueLastMessage)
    }

    fun getChatRoom(chatId: String): Observable<Chat> {
        return RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.getReference(Reference.CHATS).child(chatId),
                Chat::class.java)
    }

    fun createNewRoom(otherUser: NearbyUser): Chat {
        val summarizedCurrentUser: SummarizedUser = SummarizedUser(currentUserManager.getUser().name,
                currentUserManager.getUser().pic)
        val summarizedOtherUser: SummarizedUser = SummarizedUser(otherUser.name, otherUser.pic)

        var reference = firebaseDatabase.getReference(Reference.CHATS).push()

        val chat: Chat = Chat(reference.key!!,
                mapOf(Utils.getFirebaseId(currentUserManager.getUser().email) to summarizedCurrentUser,
                        Utils.getFirebaseId(otherUser.email) to summarizedOtherUser),
                ChatMessage("", "", 0L),
                mapOf(Utils.getFirebaseId(currentUserManager.getUser().email) to 0,
                        Utils.getFirebaseId(otherUser.email) to 0))

        reference.setValue(chat)

        firebaseDatabase.getReference(Reference.USERS)
                .child(Utils.getFirebaseId(currentUserManager.getUser().email))
                .child(Child.CHATS)
                .updateChildren(mapOf(Utils.getFirebaseId(otherUser.email) to reference.key))

        firebaseDatabase.getReference(Reference.USERS)
                .child(Utils.getFirebaseId(otherUser.email))
                .child(Child.CHATS)
                .updateChildren(mapOf(Utils.getFirebaseId(currentUserManager.getUser().email) to reference.key))

        return chat
    }

    fun registerRoomChildEventListener(listener: ChildEventListener, roomId: String, listenerId: String? = null): Boolean {
        return registerChildEventListener(firebaseDatabase.getReference(FirebaseHelper.Reference.MESSAGES)
                .child(roomId).orderByChild(FirebaseHelper.Child.TIMESTAMP), listener, listenerId
                ?: roomId)
    }

    fun unregisterRoomChildEventListener(listener: ChildEventListener, roomId: String): Unit {
        Timber.d("Unregistering room $roomId")
        removeChildListeners(firebaseDatabase.getReference(FirebaseHelper.Reference.MESSAGES)
                .child(roomId), listener)
        childEventListeners.remove(roomId)
    }

    fun registerRoomValueEventListener(listener: ValueEventListener, roomId: String, listenerId: String? = null): Boolean {
        return registerValueEventListener(firebaseDatabase.getReference(FirebaseHelper.Reference.MESSAGES)
                .child(roomId).orderByChild(FirebaseHelper.Child.TIMESTAMP), listener, listenerId
                ?: roomId)
    }

    fun addRoomSingleValueEventListener(listener: ValueEventListener, roomId: String): Unit {
        firebaseDatabase.getReference(FirebaseHelper.Reference.MESSAGES)
                .child(roomId).addListenerForSingleValueEvent(listener)
    }

    fun unregisterRoomValueEventListener(listener: ValueEventListener, roomId: String): Unit {
        Timber.d("Unregistering room $roomId")
        removeValueListeners(firebaseDatabase.getReference(FirebaseHelper.Reference.MESSAGES)
                .child(roomId), listener)
        valueEventListeners.remove(roomId)
    }

    fun registerNewChatRoomChildEventListener(listener: ChildEventListener, _userId: String? = null): Unit {
        var userId: String? = _userId
        if (userId == null) {
            userId = currentUserManager.getUserId()
        }

        registerChildEventListener(firebaseDatabase.getReference(FirebaseHelper.Reference.USERS)
                .child(userId)
                .child(FirebaseHelper.Child.CHATS), listener, "myChats")
    }

    fun registerNewUsersChildEventListener(listener: ChildEventListener): Unit {
        registerChildEventListener(firebaseDatabase.getReference(FirebaseHelper.Reference.GEOHASHES).child(currentUserManager.getUser().geohash),
                listener, "nearbyUsers")
    }

    fun messageDelivered(chatId: String): Unit {
        val mDeliveredRef = firebaseDatabase.getReference(Reference.CHATS).child(chatId).child(Child.DELIVERED_MESSAGES)

        mDeliveredRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var delivered = dataSnapshot.child(currentUserManager.getUserId()).value as Long
                mDeliveredRef.child(currentUserManager.getUserId()).setValue(++delivered)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        })
    }

    /**************************************/


    /**************** Listeners ****************/

    private fun registerChildEventListener(query: Query, listener: ChildEventListener, listenerIdentifier: String): Boolean {
        if (childEventListeners.containsKey(listenerIdentifier)) {
            Timber.d("Removing and registering event listener $listenerIdentifier again")
            firebaseDatabase.reference.removeEventListener(listener)
        }
        childEventListeners.put(listenerIdentifier, listener)
        query.addChildEventListener(listener)
        return true
    }

    private fun registerValueEventListener(query: Query, listener: ValueEventListener, listenerIdentifier: String): Boolean {
        if (!valueEventListeners.containsKey(listenerIdentifier)) {
            Timber.d("Registering value listener... $listenerIdentifier")
            valueEventListeners.put(listenerIdentifier, listener)
            query.addValueEventListener(listener)
            return true
        }
        Timber.d("Listener already registered. Skipping.")
        return false
    }

    fun removeChildListeners(query: Query, listener: ChildEventListener): Unit {
        query.removeEventListener(listener)
    }

    fun removeValueListeners(query: Query, listener: ValueEventListener): Unit {
        query.removeEventListener(listener)
    }

    fun removeAllListeners(): Unit {
        for ((listenerIdentifier, listener) in childEventListeners) {
            firebaseDatabase.reference.removeEventListener(listener)
        }

        for ((listenerIdentifier, listener) in valueEventListeners) {
            firebaseDatabase.reference.removeEventListener(listener)
        }
    }

    /*******************************************/

}
