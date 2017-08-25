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

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_chat.view.*
import kotlinx.android.synthetic.main.item_user.view.*
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

    var chats = mutableListOf<Chat>()
    /* Map of chats to speed up access. <chatId, index> */
    var chatsMapped = mutableMapOf<String, Int>()

    lateinit var chatViewHolder: ChatViewHolder

    @Inject
    lateinit var currentUserManager: CurrentUserManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ChatViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        chatViewHolder = ChatViewHolder(itemView)
        return chatViewHolder
    }

    override fun onBindViewHolder(holder: ChatAdapter.ChatViewHolder, position: Int) {
        holder.bindChat(chats[position])
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    fun setLastMessage(lastMessage: ChatMessage, chatId: String): Unit {
        if(!chatsMapped.containsKey(chatId)) {
            //Look if chatMapped was not mapped. It happens when the chat is start for first time
            for(chat in chats) {
                if(chat.id == chatId) {
                    chatsMapped.put(chat.id, chats.indexOf(chat))
                    break
                }
            }
        }
        val index: Int = chatsMapped.get(chatId)!!
        chats.get(index).lastMessage = lastMessage
        this.notifyItemChanged(index)
    }

    fun updateUnreadMessages(unreadMessages: Int, chatId: String) {
        if(chatsMapped.containsKey(chatId)) {
            val index: Int = chatsMapped.get(chatId)!!
            //chats.get(index).unreadMessages = unreadMessages.toString()
            this.notifyItemChanged(index)
        }
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindChat(chat: Chat) {

            var otherUserIndex: Int = 0
            if (chat.users.keys.indexOf(currentUserManager.getUserId()) == 0) otherUserIndex = 1

            chatsMapped.put(chat.id, chats.indexOf(chat))

            itemView.nameChatTv.text =
                chat.users.get(chat.users.keys.elementAt(otherUserIndex))?.name ?: ""
            itemView.lastMessageChatTv.text = chat.lastMessage.message
            itemView.lastMessageTimeTv.text = Date().formattedTime(itemView.ctx, chat.lastMessage.timestamp)

            updateUnreadMessages(chat.id)

            itemView.chatImg.loadUrlAndResizeCircle(chat.users.get(chat.users.keys.elementAt(otherUserIndex))?.pic,
                itemView.ctx.resources.getDimension(R.dimen.image_width_chat).toInt()) {
                request -> request.transform(CircleTransform())
            }

            itemView.setOnClickListener {
                itemView.ctx.startActivity<RoomActivity>("chat" to chat)
                MessagesManager.readMessages(chat.id)
                updateUnreadMessages(chat.id)
            }
        }

        fun updateUnreadMessages(chatId: String) {
            if(MessagesManager.getUnreadMessages(chatId) != 0) {
                itemView.unreadChatTv.text = MessagesManager.getUnreadMessages(chatId).toString()
                itemView.unreadChatTv.visibility = View.VISIBLE
            } else {
                itemView.unreadChatTv.visibility = View.GONE
            }
        }

    }
}
