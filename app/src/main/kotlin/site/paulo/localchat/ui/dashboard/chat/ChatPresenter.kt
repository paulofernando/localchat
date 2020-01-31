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

package site.paulo.localchat.ui.dashboard.chat

import com.anupcowkur.reservoir.Reservoir
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.MessagesManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.exception.MissingCurrentUserException
import site.paulo.localchat.injection.ConfigPersistent
import timber.log.Timber
import javax.inject.Inject


@ConfigPersistent
class ChatPresenter
@Inject
constructor(private val dataManager: DataManager,
            private val currentUserManager: CurrentUserManager) : ChatContract.Presenter() {

    val loaded = mutableMapOf<String?, Boolean>()

    override fun loadChatRooms(userId: String) {
        dataManager.getUser(userId).toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(onNext = {
                if(it.chats.isEmpty())
                    view.showChatsEmpty()
                else
                    for((key) in it.chats)
                        loaded[key] = false

            }, onError = {
                view.showError()
                Timber.e(it, "There was an error loading chats from an user.")
            }).addTo(compositeDisposable)

    }

    override fun loadChatRoom(chatId: String) {
        dataManager.getChatRoom(chatId).toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeBy(onNext = {
                    val childEventListener = ChatChildEventListener(dataManager, currentUserManager.getUserId(), chatId, this)

                    val allLoadedListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            loaded[it.id] = true
                            Timber.i("All data loaded from chat ${it.id}")
                            val chatMessage: ChatMessage = dataSnapshot.children.elementAt(0).getValue(ChatMessage::class.java)!!
                            val currentUser = currentUserManager.getUser()
                            if (chatMessage.owner != currentUserManager.getUserId())
                                if (!currentUser!!.chats.containsKey(chatMessage.owner)) {
                                    currentUser.chats.put(chatMessage.owner, it.id)
                                }
                        }

                        override fun onCancelled(dataSnapshot: DatabaseError) {}
                    }

                    Reservoir.put(it.id, it) //persisting chats

                    //register room if it is not registered
                    dataManager.registerRoomChildEventListener(childEventListener, it.id)

                    dataManager.addRoomSingleValueEventListener(allLoadedListener, it.id)

                    view.showChat(it)
                }, onError = {
                    Timber.e(it, "There was an error loading a chat room.")
                    view.showError()
                }).addTo(compositeDisposable)
    }

    override fun listenNewChatRooms(userId: String) {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val currentUser = currentUserManager.getUser() ?: return
                loadChatRoom(dataSnapshot.value.toString())
                if (((loaded[dataSnapshot.value.toString()] != null) && (loaded[dataSnapshot.value.toString()]!!))
                        && (!currentUser.chats.containsKey(dataSnapshot.key)))
                    currentUser.chats[dataSnapshot.key!!] = dataSnapshot.value.toString()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) { }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }

        dataManager.registerNewChatRoomChildEventListener(childEventListener, userId)
        Timber.i("Listening for new chats...")
    }

    override fun loadProfilePicture(chatList: List<Chat>) {
        //TODO
    }

    private val compositeDisposable = CompositeDisposable()

    override fun detachView() {
        super.detachView()
        compositeDisposable.clear()
    }

    private class ChatChildEventListener
    constructor(private val dataManager: DataManager, private val userId: String,
                private val chatId: String, private val presenter: ChatPresenter) : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val chatMessage: ChatMessage = dataSnapshot.getValue(ChatMessage::class.java)!!
            presenter.view.messageReceived(chatMessage, chatId)
            if ((presenter.loaded[chatId] != null) && presenter.loaded[chatId]!!) { //only register message delivered if is a new message.
                dataManager.messageDelivered(chatId)
                if (chatMessage.owner != userId) { //not mine
                    MessagesManager.unreadMessages(chatId, userId)
                    presenter.view.notifyUser(chatMessage, chatId)
                }
            }
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onCancelled(databaseError: DatabaseError) {}
    }

}