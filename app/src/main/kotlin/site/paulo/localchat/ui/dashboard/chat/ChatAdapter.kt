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

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import kotlinx.android.synthetic.main.item_chat.view.*
import org.jetbrains.anko.startActivity
import site.paulo.localchat.R
import site.paulo.localchat.data.MessagesManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.ui.room.RoomActivity
import site.paulo.localchat.ui.utils.*
import java.util.*
import javax.inject.Inject

class ChatAdapter
@Inject
constructor() : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private var chats: SortedList<Chat>
    /** Map of chats to speed up access. <chatId, index> */
    private var chatsMapped = mutableMapOf<String?, Int>()

    private lateinit var chatViewHolder: ChatViewHolder

    @Inject
    lateinit var currentUserManager: CurrentUserManager

    @Inject
    lateinit var messagesManager: MessagesManager

    init {
        chats = SortedList(Chat::class.java, object : SortedListAdapterCallback<Chat>(this) {
            override fun compare(o1: Chat, o2: Chat): Int =
                    (o2.lastMessage.timestamp - o1.lastMessage.timestamp).toInt()

            override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean = oldItem.id == newItem.id

            override fun areItemsTheSame(item1: Chat, item2: Chat): Boolean = item1 == item2
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat, parent, false)
        chatViewHolder = ChatViewHolder(itemView)
        return chatViewHolder
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bindChat(chats[position])
    }

    override fun getItemCount(): Int {
        return chats.size()
    }

    fun setLastMessage(lastMessage: ChatMessage, chatId: String): Unit {
        if (!chatsMapped.containsKey(chatId)) {
            //Checks if chatMapped was not mapped. It happens when the chat is start for first time
            for (i in 0 until chats.size()) {
                if (chats[i].id == chatId) {
                    chatsMapped[chats[i].id] = chats.indexOf(chats[i])
                    break
                }
            }
        }
        val index: Int = chatsMapped[chatId] ?: return
        val updatedChat = chats[index]
        updatedChat.lastMessage = lastMessage
        chats.updateItemAt(index, updatedChat)

        this.notifyDataSetChanged()
    }

    fun addChat(chat: Chat) {
        this.chats.add(chat)
        notifyDataSetChanged()
    }

    fun updateUnreadMessages(unreadMessages: Int, chatId: String) {
        if (chatsMapped.containsKey(chatId)) {
            val index: Int = chatsMapped[chatId]!!
            //chats.get(index).unreadMessages = unreadMessages.toString()
            this.notifyItemChanged(index)
        }
    }

    inner class ChatViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bindChat(chat: Chat) {
            val chatFriend = Utils.getChatFriend(currentUserManager.getUserId(), chat)
            chatsMapped[chat.id] = chats.indexOf(chat)

            itemView.nameChatTv.text = chatFriend?.name ?: ""
            itemView.lastMessageChatTv.text = chat.lastMessage.message
            itemView.lastMessageTimeTv.text = Date().formattedTime(itemView.ctx, chat.lastMessage.timestamp)

            updateUnreadMessages(chat.id, currentUserManager.getUserId())

            itemView.chatImg.loadUrlAndResizeCircle(chatFriend?.pic, itemView.ctx.resources.getDimension(R.dimen.image_width_chat).toInt()) { request ->
                request.transform(CircleTransform())
            }

            itemView.setOnClickListener {
                itemView.ctx.startActivity<RoomActivity>("chatId" to chat.id, "otherUser" to chatFriend)
                messagesManager.readMessages(chat.id, currentUserManager.getUserId())
                updateUnreadMessages(chat.id, currentUserManager.getUserId())
            }
        }

        private fun updateUnreadMessages(chatId: String, userId: String) {
            if (messagesManager.getUnreadMessages(chatId, userId) != 0) {
                itemView.unreadChatTv.text = messagesManager.getUnreadMessages(chatId, userId).toString()
                itemView.unreadChatTv.visibility = View.VISIBLE
            } else {
                itemView.unreadChatTv.visibility = View.GONE
            }
        }
    }
}
