package site.paulo.localchat.ui.room

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_room_message.view.*
import site.paulo.localchat.R
import site.paulo.localchat.data.model.chatgeo.ChatMessage
import javax.inject.Inject

class RoomAdapter
@Inject
constructor() : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    var messages =  mutableListOf<ChatMessage>()

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
            itemView.roomMessage.text = message.message
        }
    }
}