package site.paulo.localchat.ui.signin

import com.google.firebase.database.FirebaseDatabase
import site.paulo.localchat.data.model.chatgeo.ChatMessage
import javax.inject.Inject

class RoomPresenter
@Inject
constructor(private val firebase: FirebaseDatabase) : RoomContract.Presenter() {

    override fun loadMessages() {
        view.showMessages()
    }

    override fun sendMessage(message: ChatMessage) {
        firebase.getReference("chats").child("General").child("messages").push().setValue(message)
        view.cleanMessageField()
    }

}