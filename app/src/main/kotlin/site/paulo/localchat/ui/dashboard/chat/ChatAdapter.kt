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
import site.paulo.localchat.ui.utils.ctx
import javax.inject.Inject

class ChatAdapter
@Inject
constructor() : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    var chats = emptyList<Chat>()

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
            with(chat) {
                Picasso.with(itemView.ctx)
                    .load(chatPic)
                    .resize(itemView.ctx.resources.getDimension(R.dimen.image_width_chat).toInt(),
                        itemView.ctx.resources.getDimension(R.dimen.image_height_chat).toInt())
                    .centerCrop()
                    .transform(CircleTransform())
                    .into(itemView.image_chat)

                itemView.text_name_chat.text = name

                itemView.setOnClickListener {
                    itemView.ctx.startActivity<RoomActivity>("chat" to chat)
                }
            }
        }
    }
}
