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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import butterknife.BindView
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import site.paulo.localchat.R
import site.paulo.localchat.data.LocalDataManager
import site.paulo.localchat.data.MessagesManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.data.model.firebase.SummarizedUser
import site.paulo.localchat.ui.base.BaseFragment
import site.paulo.localchat.ui.room.RoomActivity
import site.paulo.localchat.ui.utils.Utils
import site.paulo.localchat.ui.utils.getChatFriend
import site.paulo.localchat.ui.utils.getFirebaseId
import javax.inject.Inject

class ChatFragment : BaseFragment(), ChatContract.View {

    @Inject
    lateinit var presenter: ChatPresenter

    @Inject
    lateinit var chatsAdapter: ChatAdapter

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var currentUserManager: CurrentUserManager

    @Inject
    lateinit var localDataManager: LocalDataManager

    @Inject
    lateinit var messagesManager: MessagesManager

    @BindView(R.id.chatRoomsList)
    lateinit var chatsList: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = setupFragment(inflater, container)

        chatsList.adapter = chatsAdapter
        chatsList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity as Context?)

        val email = firebaseAuth.currentUser?.email
        if (email != null) {
            presenter.loadChatRooms(Utils.getFirebaseId(email))
            presenter.listenNewChatRooms(Utils.getFirebaseId(email))
        }

        return rootView
    }

    private fun setupFragment(inflater: LayoutInflater, container: ViewGroup?): View {
        activityComponent.inject(this)
        presenter.attachView(this)
        val view = inflater.inflate(R.layout.fragment_dashboard_chats, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun showChat(chat: Chat) {
        chatsAdapter.addChat(chat)
    }

    override fun showChatsEmpty() {
        //chatsAdapter.addChats(SortedList<Chat>())
        //chatsAdapter.notifyDataSetChanged()
        //Toast.makeText(activity, R.string.empty_chat, Toast.LENGTH_LONG).show()
    }

    override fun showError() {
        Toast.makeText(activity, R.string.error_loading_chat, Toast.LENGTH_LONG).show()
    }

    override fun messageReceived(chatMessage: ChatMessage, chatId: String) {
        messagesManager.add(chatMessage, chatId)
    }

    override fun updateLastMessage(chatMessage: ChatMessage, chatId: String) {
        chatsAdapter.setLastMessage(chatMessage, chatId)
    }

    override fun notifyUser(chatMessage: ChatMessage, chatId: String) {
        if (!isResumed) { //only notify user if chats fragment is not being shown at moment
            val chatFriend = getFriendUser(chatId) ?: return
            val ctx = context ?: return

            //Intent to be open when the user clicks on notification
            val intent = Intent(ctx, RoomActivity::class.java)
            intent.putExtra("chatId", chatId)
            val pendingIntent = PendingIntent.getActivity(ctx, chatId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val builder: NotificationCompat.Builder = NotificationCompat.Builder(ctx, "MessageReceivedChannel")
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(chatFriend.name)
                    .setContentText(chatMessage.message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)

            val notificationManager = NotificationManagerCompat.from(ctx)

            // notificationId must be an unique int for each notification
            notificationManager.notify(chatId.hashCode(), builder.build())
        }
    }

    private fun getFriendUser(chatId: String): SummarizedUser? {
        var currentChat: Chat? = null
        if (localDataManager.contains(chatId)) {
            currentChat = localDataManager.get(chatId, Chat::class.java)
        }

        if (currentChat != null) {
            return Utils.getChatFriend(currentUserManager.getUserId(), currentChat)
        }

        return null
    }

}