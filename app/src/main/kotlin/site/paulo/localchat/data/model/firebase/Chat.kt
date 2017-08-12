package site.paulo.localchat.data.model.firebase

import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class Chat(val id: String = "",
    val users: Map<String, SummarizedUser> = emptyMap(),
    var lastMessage: String = "",
    val receivedMessages: Map<String, Int> = emptyMap(),
    var unreadMessages: String = "") //TODO remove
    : PaperParcelable {

    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(Chat::class.java)
    }

}
