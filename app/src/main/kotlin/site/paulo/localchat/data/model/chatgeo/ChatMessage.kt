package site.paulo.localchat.data.model.chatgeo

import com.google.firebase.database.Exclude
import nz.bradcampbell.paperparcel.PaperParcelable

data class ChatMessage(val owner: String = "", val message: String = "",
    val timestamp: Long = 0L) : PaperParcelable {

    companion object {
       @JvmField val CREATOR = PaperParcelable.Creator(ChatMessage::class.java)
    }
    
}