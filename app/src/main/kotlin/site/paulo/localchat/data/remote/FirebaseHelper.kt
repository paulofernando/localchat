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
import javax.inject.Inject
import javax.inject.Singleton

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
            NAME, AGE
        }
    }

    private var childEventListeners: ArrayList<ChildEventListener> = ArrayList<ChildEventListener>()
    private var valueEventListeners: ArrayList<ValueEventListener> = ArrayList<ValueEventListener>()

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

    fun createNewRoom(otherUser:User): Chat {
        val summarizedCurrentUser: SummarizedUser = SummarizedUser(currentUserManager.getUser().name,
            currentUserManager.getUser().pic)
        val summarizedOtherUser:SummarizedUser = SummarizedUser(otherUser.name, otherUser.pic)

        var reference = firebaseDatabase.getReference(Reference.CHATS).push()

        val chat:Chat = Chat(reference.key, mapOf(Utils.getFirebaseId(currentUserManager.getUser().email) to summarizedCurrentUser,
            Utils.getFirebaseId(otherUser.email) to summarizedOtherUser), "")

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

    /*******************************************/


    /**************** Chat ****************/

    fun sendMessage(message: ChatMessage, chatId: String, completionListener: DatabaseReference.CompletionListener): Unit {
        val valueMessage = mutableMapOf<String, Any>()
        valueMessage.put(Child.OWNER, message.owner)
        valueMessage.put(Child.MESSAGE, message.message)
        valueMessage.put(Child.TIMESTAMP, ServerValue.TIMESTAMP)

        val valueLastMessage = mutableMapOf<String, Any>()
        valueLastMessage.put(Child.LAST_MESSAGE, message.owner + ": " + message.message)

        firebaseDatabase.getReference(Reference.MESSAGES).child(chatId).push().setValue(valueMessage, completionListener)
        firebaseDatabase.getReference(Reference.CHATS).child(chatId).updateChildren(valueLastMessage)
    }

    fun getChatRoom(chatId:String): Observable<Chat> {
        return RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.getReference(Reference.CHATS).child(chatId),
            Chat::class.java)
    }

    /**************************************/


    /**************** Listeners ****************/

    fun registerChildEventListener(query: Query, listener: ChildEventListener): Unit {
        childEventListeners.add(listener)
        query.addChildEventListener(listener)
    }

    fun registerValueEventListener(query: Query, listener: ValueEventListener): Unit {
        valueEventListeners.add(listener)
        query.addValueEventListener(listener)
    }

    fun removeAllListeners(): Unit {
        childEventListeners.forEach { firebaseDatabase.getReference().removeEventListener(it) }
        valueEventListeners.forEach { firebaseDatabase.getReference().removeEventListener(it) }
    }

    /*******************************************/

}
