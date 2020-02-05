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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.LocalDataManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.data.model.firebase.SummarizedUser
import timber.log.Timber
import javax.inject.Inject

class RoomPresenter
@Inject
constructor(private val dataManager: DataManager,
            private val currentUserManager: CurrentUserManager,
            private val firebaseStorage: FirebaseStorage,
            private val localDataManager: LocalDataManager) : RoomContract.Presenter() {

    private var childEventListener: ChildEventListener? = null

    /** Get data from an specific chat room. We can use it in case of some issue
     * while storing chat data locally. **/
    override fun getChatData(chatId: String) {
        if (!localDataManager.contains(chatId)) {
            dataManager.getChatRoom(chatId).toObservable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeBy(onNext = {
                        if (it.id != "") {
                            view.showChat(it.id)
                            if (localDataManager.contains(it.id)) {
                                localDataManager.put(it.id, it)
                            }
                        } else view.showEmptyChatRoom()
                    }, onError = {
                        Timber.e(it, "There was an error loading a chat room.")
                        view.showError()
                    }
                    ).addTo(compositeDisposable)
        } else {
            view.showChat(localDataManager.get(chatId, Chat::class.java).id)
        }
    }

    override fun sendMessage(message: ChatMessage, chatId: String) {
        if(message.message != "") {
            val completionListener = DatabaseReference.CompletionListener { _, _ ->
                view.messageSent(message)
            }
            dataManager.sendMessage(message, chatId, completionListener)
            view.cleanMessageField()
        }
    }

    override fun registerMessagesListener(chatId: String) {
        childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, s: String?) {
                val chatMessage: ChatMessage = snapshot.getValue(ChatMessage::class.java)!!
                messageReceived(chatMessage)
                if(!chatMessage.owner.equals(currentUserManager.getUserId()))
                    Timber.d("Message received in $chatId-intern")

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }

        dataManager.registerRoomChildEventListener(childEventListener as ChildEventListener, chatId, chatId + "-intern")
        Timber.d("Listening chat room $chatId-intern")
    }

    override fun unregisterMessagesListener(chatId: String) {
        if(childEventListener != null)
            dataManager.unregisterRoomChildEventListener(childEventListener!!, chatId)
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

    override fun createNewRoom(otherUser: SummarizedUser, otherEmail: String): Chat {
        return dataManager.createNewRoom(otherUser, otherEmail)
    }

    private val compositeDisposable = CompositeDisposable()

    override fun detachView() {
        super.detachView()
        compositeDisposable.clear()
    }

}