package site.paulo.localchat.ui.user

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_chat.view.*
import org.jetbrains.anko.startActivity
import site.paulo.localchat.R
import site.paulo.localchat.data.model.chatgeo.Chat
import site.paulo.localchat.ui.room.RoomActivity
import site.paulo.localchat.ui.utils.CircleTransform
import site.paulo.localchat.ui.utils.Utils
import site.paulo.localchat.ui.utils.ctx
import site.paulo.localchat.ui.utils.getCurrentUserId
import javax.inject.Inject

class ChatAdapter
@Inject
constructor() : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    var chats = mutableListOf<Chat>()

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
            if (chat.users.keys.indexOf(Utils.getCurrentUserId()) == 0) otherUserIndex = 1 //TODO change to getCurrentUserId

            itemView.text_name_chat.text =
                chat.users.get(chat.users.keys.elementAt(otherUserIndex))!!.name

            itemView.text_last_message_chat.text = chat.lastMessage

            Picasso.with(itemView.ctx)
                .load(chat.users.get(chat.users.keys.elementAt(otherUserIndex))!!.profilePic)
                .resize(itemView.ctx.resources.getDimension(R.dimen.image_width_chat).toInt(),
                    itemView.ctx.resources.getDimension(R.dimen.image_height_chat).toInt())
                .centerCrop()
                .transform(CircleTransform())
                .into(itemView.image_chat)

            itemView.setOnClickListener {
                itemView.ctx.startActivity<RoomActivity>("chat" to chat)
            }

        }
    }
}