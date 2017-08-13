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

package site.paulo.localchat.ui.user

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.FunctionSubscriber
import rx.lang.kotlin.addTo
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.injection.ConfigPersistent
import site.paulo.localchat.ui.dashboard.nearby.ChatContract
import timber.log.Timber
import javax.inject.Inject


@ConfigPersistent
class ChatPresenter
@Inject
constructor(private val dataManager: DataManager,
            private val currentUserManager: CurrentUserManager) : ChatContract.Presenter() {

    override fun loadChatRooms(userId:String) {
        dataManager.getUser(userId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(FunctionSubscriber<User>()
                .onNext {
                    if(it.chats.isEmpty()) {
                        view.showChatsEmpty()
                    } else {
                        it.chats.forEach {
                            loadChatRoom(it.value)
                        }
                    }
                }
                .onError {
                    view.showError()
                    Timber.e(it, "There was an error loading chats from an user.")
                }
            ).addTo(compositeSubscription)

    }

    override fun loadChatRoom(chatId: String) {
        dataManager.getChatRoom(chatId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(FunctionSubscriber<Chat>()
                .onNext {
                    val childEventListener = object : ChildEventListener {
                        override fun onChildAdded(snapshot: DataSnapshot, s: String?) {
                            val chatMessage: ChatMessage = snapshot.getValue(ChatMessage::class.java)
                            view.messageReceived(chatMessage, chatId)
                            if(chatMessage.owner != currentUserManager.getUserId()) {
                                dataManager.messageDelivered(chatId)
                            }
                        }

                        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
                        override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
                        override fun onCancelled(databaseError: DatabaseError) {}
                    }

                    if(dataManager.registerRoomChildEventListener(childEventListener, it.id))
                        view.showChat(it)
                }
                .onError {
                    Timber.e(it, "There was an error loading a chat room.")
                    view.showError()
                }
            ).addTo(compositeSubscription)
    }

    override fun listenNewChatRooms(userId: String) {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, s: String?) {
                loadChatRoom(snapshot.value.toString())
                currentUserManager.getUser().chats.put(snapshot.key, snapshot.value.toString())
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
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

    private val compositeSubscription = CompositeSubscription()

    override fun detachView() {
        super.detachView()
        compositeSubscription.clear()
    }

}