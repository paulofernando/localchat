package site.paulo.localchat.ui.room

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import site.paulo.localchat.data.model.chatgeo.ChatMessage
import site.paulo.localchat.ui.room.RoomContract
import javax.inject.Inject

class RoomPresenter
@Inject
constructor(private val firebase: FirebaseDatabase) : RoomContract.Presenter() {

    override fun loadMessages() {
        //view.showMessages()
    }

    override fun sendMessage(msg: ChatMessage, chatId: String) {
        if(!msg.message.equals("")) {
            val value = mutableMapOf<String, Any>()
            value.put("owner", msg.owner)
            value.put("message", msg.message)
            value.put("timestamp", ServerValue.TIMESTAMP)

            firebase.getReference("messages").child(chatId).push().setValue(value)
            view.cleanMessageField()
        }
    }

    override fun registerRoomListener(chatId: String) {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, s: String?) {
                view.addMessage(snapshot.getValue(ChatMessage::class.java))
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {}

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        firebase.getReference("messages")
            .child(chatId)
            .addChildEventListener(childEventListener)
    }

}