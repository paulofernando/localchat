package site.paulo.localchat.data.model.firebase

import com.google.firebase.database.Exclude
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class ChatMessage(val owner: String = "", val message: String = "",
    val timestamp: Long = 0L) : PaperParcelable {

    companion object {
       @JvmField val CREATOR = PaperParcelable.Creator(ChatMessage::class.java)
    }
    
}