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
import com.kelvinapps.rxfirebase.RxFirebaseChildEvent
import com.kelvinapps.rxfirebase.RxFirebaseDatabase
import rx.Observable
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.data.model.firebase.SummarizedUser
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.ui.utils.Utils
import site.paulo.localchat.ui.utils.getFirebaseId
import timber.log.Timber
import java.util.ArrayList
import java.util.HashMap
import javax.inject.Inject
import javax.inject.Singleton
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot



@Singleton
class FirebaseHelper @Inject constructor(val firebaseDatabase: FirebaseDatabase,
    val currentUserManager: CurrentUserManager,
    val firebaseAuth: FirebaseAuth) {

    object Reference {
        val CHATS = "chats"
        val MESSAGES = "messages"
        val USERS = "users"
    }

    object Child {
        val AGE = "age"
        val CHATS = "chats"
        val DELIVERED_MESSAGES = "deliveredMessages"
        val EMAIL = "email"
        val GENDER = "gender"
        val ID = "id"
        val LAST_MESSAGE = "lastMessage"
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

    fun getUser(userId:String): Observable<User> {
        return RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.getReference(Reference.USERS).child(userId),
            User::class.java)
    }

    fun registerUser(user:User): Unit {
        val value = mutableMapOf<String, Any>()
        value.put(Child.EMAIL, user.email)
        value.put(Child.NAME, user.name)
        value.put(Child.AGE, user.age)
        value.put(Child.GENDER, user.gender)
        value.put(Child.PIC, "https://api.adorable.io/avatars/240/" + Utils.getFirebaseId(user.email) + ".png")

        val completionListener = DatabaseReference.CompletionListener { databaseError, databaseReference ->
            Timber.e("User " + user.email + "registered")
        }
        firebaseDatabase.getReference(Reference.USERS).child(Utils.getFirebaseId(user.email)).setValue(value, completionListener)
    }

    fun authenticateUser(email: String, password: String): Observable<AuthResult> {
        return RxFirebaseAuth.signInWithEmailAndPassword(firebaseAuth, email, password)
    }

    fun updateUserData(dataType: UserDataType, value:String, completionListener: DatabaseReference.CompletionListener): Unit {
        when(dataType) {
            UserDataType.NAME -> {
                val v = mutableMapOf<String, Any>()
                v.put(Child.NAME, value)
                firebaseDatabase.getReference(Reference.USERS)
                    .child(currentUserManager.getUserId())
                    .updateChildren(v, completionListener)
            }
            UserDataType.AGE -> {
                val v = mutableMapOf<String, Any>()
                v.put(Child.AGE, value.toInt())
                firebaseDatabase.getReference(Reference.USERS)
                    .child(currentUserManager.getUserId()).updateChildren(v, completionListener)
            }
            UserDataType.PIC -> {
                val v = mutableMapOf<String, Any>()
                v.put(Child.PIC, value)
                firebaseDatabase.getReference(Reference.USERS)
                    .child(currentUserManager.getUserId()).updateChildren(v, completionListener)
            }
            else -> Timber.e("Invalid UserDataType")
        }
    }

    fun updateToken(token: String?): Unit {
        val completionListener = DatabaseReference.CompletionListener { databaseError, databaseReference ->
            Timber.d("Token updated")
        }
        if(token != null) {
            val v = mutableMapOf<String, Any>()
            v.put(Child.TOKEN, token)
            firebaseDatabase.getReference(Reference.USERS)
                .child(currentUserManager.getUserId())
                .updateChildren(v, completionListener)
        }
    }

    fun updateProfilePic(url: String?): Unit {
        val completionListener = DatabaseReference.CompletionListener { databaseError, databaseReference ->
            Timber.d("Profile pic updated")
        }
        if(url != null) {
            val v = mutableMapOf<String, Any>()
            v.put(Child.PIC, url)
            firebaseDatabase.getReference(Reference.USERS)
                .child(currentUserManager.getUserId())
                .updateChildren(v, completionListener)
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
        valueMessage.put(Child.OWNER, message.owner)
        valueMessage.put(Child.MESSAGE, message.message)
        valueMessage.put(Child.TIMESTAMP, ServerValue.TIMESTAMP)

        val valueLastMessage = mutableMapOf<String, Any>()
        valueLastMessage.put(Child.LAST_MESSAGE, message.message)

        firebaseDatabase.getReference(Reference.MESSAGES).child(chatId).push().setValue(valueMessage, completionListener)
        firebaseDatabase.getReference(Reference.CHATS).child(chatId).updateChildren(valueLastMessage)
    }

    fun getChatRoom(chatId:String): Observable<Chat> {
        return RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.getReference(Reference.CHATS).child(chatId),
            Chat::class.java)
    }

    fun createNewRoom(otherUser:User): Chat {
        val summarizedCurrentUser: SummarizedUser = SummarizedUser(currentUserManager.getUser().name,
            currentUserManager.getUser().pic)
        val summarizedOtherUser:SummarizedUser = SummarizedUser(otherUser.name, otherUser.pic)

        var reference = firebaseDatabase.getReference(Reference.CHATS).push()

        val chat:Chat = Chat(reference.key,
                mapOf(Utils.getFirebaseId(currentUserManager.getUser().email) to summarizedCurrentUser,
                        Utils.getFirebaseId(otherUser.email) to summarizedOtherUser),
                "",
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

    fun registerRoomChildEventListener(listener: ChildEventListener, roomId: String): Boolean {
        return registerChildEventListener(firebaseDatabase.getReference(FirebaseHelper.Reference.MESSAGES)
            .child(roomId).orderByChild(FirebaseHelper.Child.TIMESTAMP), listener, roomId)
    }

    fun registerRoomValueEventListener(listener: ValueEventListener, roomId: String): Boolean {
        return registerValueEventListener(firebaseDatabase.getReference(FirebaseHelper.Reference.MESSAGES)
            .child(roomId).orderByChild(FirebaseHelper.Child.TIMESTAMP), listener, roomId)
    }

    fun registerNewChatRoomChildEventListener(listener: ChildEventListener, _userId: String? = null): Unit {
        var userId: String? = _userId
        if(userId == null) {
            userId = currentUserManager.getUserId()
        }

        registerChildEventListener(firebaseDatabase.getReference(FirebaseHelper.Reference.USERS)
                .child(userId)
                .child(FirebaseHelper.Child.CHATS), listener, "myChats")
    }

    fun messageDelivered(chatId: String): Unit {
        val mDeliveredRef = firebaseDatabase.getReference(Reference.CHATS).
                child(chatId).
                child(Child.DELIVERED_MESSAGES)

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
        if(!childEventListeners.containsKey(listenerIdentifier)) {
            Timber.d("Registering event listener... $listenerIdentifier")
            childEventListeners.put(listenerIdentifier, listener)
            query.addChildEventListener(listener)
            return true
        }
        Timber.d("Listener already registered. Skipping.")
        return false
    }

    private fun registerValueEventListener(query: Query, listener: ValueEventListener, listenerIdentifier: String): Boolean {
        if(!childEventListeners.containsKey(listenerIdentifier)) {
            Timber.d("Registering value listener... $listenerIdentifier")
            valueEventListeners.put(listenerIdentifier, listener)
            query.addValueEventListener(listener)
            return true
        }
        Timber.d("Listener already registered. Skipping.")
        return false
    }

    fun removeAllListeners(): Unit {
        for ((listenerIdentifier, listener) in childEventListeners) {
            firebaseDatabase.getReference().removeEventListener(listener)
        }

        for ((listenerIdentifier, listener) in valueEventListeners) {
            firebaseDatabase.getReference().removeEventListener(listener)
        }
    }

    /*******************************************/

}
