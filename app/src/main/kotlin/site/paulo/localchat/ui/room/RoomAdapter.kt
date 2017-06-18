package site.paulo.localchat.ui.room

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_room_message.view.*
import me.himanshusoni.chatmessageview.ChatMessageView
import site.paulo.localchat.R
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.ui.utils.ctx
import site.paulo.localchat.ui.utils.formattedTime
import site.paulo.localchat.ui.utils.loadUrlAndCacheOffline
import java.util.Date
import javax.inject.Inject

class RoomAdapter
@Inject
constructor() : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    var messages =  mutableListOf<ChatMessage>()

    @Inject
    lateinit var currentUserManager: CurrentUserManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomAdapter.RoomViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room_message, parent, false)
        return RoomViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RoomAdapter.RoomViewHolder, position: Int) {
        holder.bindMessages(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindMessages(message: ChatMessage) {
            itemView.messageUserNameRoomTv.text = message.owner

            if (message.message.startsWith("https://firebasestorage.googleapis.com/")) {
                itemView.chatRoomPicImg.loadUrlAndCacheOffline(message.message)
                itemView.chatRoomPicImg.visibility = View.VISIBLE
                itemView.messageRoomTv.visibility = View.GONE
            } else {
                itemView.messageRoomTv.text = message.message
            }

            itemView.messageTimeRoomTv.text = Date().formattedTime(itemView.ctx, message.timestamp)

            if(message.owner.equals(currentUserManager.getUserId())) {
                itemView.messageBubbleRoom.setArrowPosition(ChatMessageView.ArrowPosition.RIGHT)
                itemView.messageBubbleRoom.setBackgroundColorRes(R.color.color_room_my_bubble_background,
                    R.color.color_room_my_bubble_pressed_background)
            }

        }
    }
}