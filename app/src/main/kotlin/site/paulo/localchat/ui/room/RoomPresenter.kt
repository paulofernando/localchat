package site.paulo.localchat.ui.signin

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import site.paulo.localchat.data.model.chatgeo.ChatMessage
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
            value.put("name", msg.name)
            value.put("message", msg.message)
            value.put("timestamp", ServerValue.TIMESTAMP)

            firebase.getReference("chats").child(chatId).child("messages").push().setValue(value)
            view.cleanMessageField()
        }
    }

    override fun registerRoomListener() {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, s: String?) {
                view.addMessage(snapshot.getValue(ChatMessage::class.java))
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {}

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        firebase.getReference("chats")
            .child("General")
            .child("messages")
            .addChildEventListener(childEventListener)
    }

}