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
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.ui.room.RoomActivity
import site.paulo.localchat.ui.utils.CircleTransform
import site.paulo.localchat.ui.utils.ctx
import site.paulo.localchat.ui.utils.loadUrlAndResize
import javax.inject.Inject

class ChatAdapter
@Inject
constructor() : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    var chats = mutableListOf<Chat>()

    @Inject
    lateinit var currentUserManager: CurrentUserManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ChatViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatAdapter.ChatViewHolder, position: Int) {
        holder.bindChat(chats[position])
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindChat(chat: Chat) {

            var otherUserIndex: Int = 0
            if (chat.users.keys.indexOf(currentUserManager.getUserId()) == 0) otherUserIndex = 1

            itemView.nameChatTv.text =
                chat.users.get(chat.users.keys.elementAt(otherUserIndex))?.name ?: ""

            itemView.lastMessageChatTv.text = chat.lastMessage

            itemView.chatImg.loadUrlAndResize(chat.users.get(chat.users.keys.elementAt(otherUserIndex))?.pic,
                itemView.ctx.resources.getDimension(R.dimen.image_width_chat).toInt()) {
                request -> request.transform(CircleTransform())
            }

            itemView.setOnClickListener {
                itemView.ctx.startActivity<RoomActivity>("chat" to chat)
            }

        }
    }
}
