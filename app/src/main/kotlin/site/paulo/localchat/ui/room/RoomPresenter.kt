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

import android.net.Uri
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.FunctionSubscriber
import rx.schedulers.Schedulers
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.data.model.firebase.NearbyUser
import timber.log.Timber
import javax.inject.Inject

class RoomPresenter
@Inject
constructor(private val dataManager: DataManager,
    private val currentUserManager: CurrentUserManager,
    private val firebaseStorage: FirebaseStorage) : RoomContract.Presenter() {

    private var childEventListener: ChildEventListener? = null

    override fun getChatData(chatId: String) {
        dataManager.getChatRoom(chatId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(FunctionSubscriber<Chat>()
                .onNext {
                    if(it.id != "") view.showChat(it)
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

    override fun registerMessagesListener(roomId: String) {
        childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, s: String?) {
                val chatMessage: ChatMessage = snapshot.getValue(ChatMessage::class.java)!!
                messageReceived(chatMessage)
                if(!chatMessage.owner.equals(currentUserManager.getUserId()))
                    Timber.d("Message received in $roomId-intern")

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }

        dataManager.registerRoomChildEventListener(childEventListener as ChildEventListener, roomId, roomId + "-intern")
        Timber.d("Listening chat room $roomId-intern")
    }

    override fun unregisterMessagesListener(roomId: String) {
        if(childEventListener != null)
            dataManager.unregisterRoomChildEventListener(childEventListener!!, roomId)
    }

    /*override fun registerMessagesListener(roomId: String) {
        val messages: MutableList<ChatMessage>? = MessagesManager.registerListener(this, roomId)
        view.loadOldMessages(messages)
        Timber.d("Listening chat room $roomId")
    }*/

    override fun messageReceived(chatMessage: ChatMessage) {
        view.addMessage(chatMessage)
    }

    override fun uploadImage(selectedImageUri: Uri, roomId: String) {
        // Get a reference to the location where we'll store our photos
        var storageRef = firebaseStorage.getReference("chat_pics")
        // Get a reference to store file at chat_photos/<FILENAME>
        val photoRef = storageRef.child(selectedImageUri.lastPathSegment!!)

        view.showLoadingImage()

        // Upload file to Firebase Storage
        photoRef.putFile(selectedImageUri).addOnSuccessListener { taskSnapshot ->
            Timber.i("Image sent successfully!")
            val downloadUrl = taskSnapshot.storage.downloadUrl
            sendMessage(ChatMessage(currentUserManager.getUserId(), downloadUrl.toString()), roomId)
            view.hideLoadingImage()
        }
    }

    override fun createNewRoom(otherUser: NearbyUser): Chat {
        return dataManager.createNewRoom(otherUser)
    }

}