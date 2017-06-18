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

package site.paulo.localchat.ui.room

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.FunctionSubscriber
import rx.schedulers.Schedulers
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.data.remote.FirebaseHelper
import timber.log.Timber
import javax.inject.Inject

class RoomPresenter
@Inject
constructor(private val firebaseDatabase: FirebaseDatabase, private val dataManager: DataManager) : RoomContract.Presenter() {

    override fun getChatData(chatId: String) {
        dataManager.getChatRoom(chatId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(FunctionSubscriber<Chat>()
                .onNext {
                    if(!it.id.equals("")) view.showChat(it)
                    else view.showEmptyChatRoom()
                }
                .onError {
                    Timber.e(it, "There was an error loading a chat room.")
                    view.showError()
                }
            )
    }

    override fun sendMessage(message: ChatMessage, chatId: String) {
        if(!message.message.equals("")) {
            val completionListener = DatabaseReference.CompletionListener { databaseError, databaseReference ->
                view.messageSent(message)
            }
            dataManager.sendMessage(message, chatId, completionListener)
            view.cleanMessageField()
        }
    }

    override fun registerRoomListener(chatId: String) {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, s: String?) {
                view.addMessage(snapshot.getValue(ChatMessage::class.java))
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }



        dataManager.registerChildEventListener(firebaseDatabase.getReference(FirebaseHelper.Reference.MESSAGES)
            .child(chatId).orderByChild(FirebaseHelper.Child.TIMESTAMP), childEventListener)
    }

    override fun createNewRoom(otherUser: User): Chat {
        return dataManager.createNewRoom(otherUser)
    }

}