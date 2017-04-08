package site.paulo.localchat.ui.signin

import android.os.UserManager
import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.leakcanary.internal.LeakCanaryInternals.showNotification
import site.paulo.localchat.data.model.chatgeo.ChatMessage
import java.util.ArrayList
import javax.inject.Inject

class RoomPresenter
@Inject
constructor(private val firebase: FirebaseDatabase) : RoomContract.Presenter() {

    override fun loadMessages() {
        //view.showMessages()
    }

    override fun sendMessage(message: ChatMessage) {
        firebase.getReference("chats").child("General").child("messages").push().setValue(message)
        view.cleanMessageField()
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