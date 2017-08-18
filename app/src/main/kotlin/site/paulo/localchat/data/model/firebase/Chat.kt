package site.paulo.localchat.data.model.firebase

import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class Chat(val id: String = "",
                val users: Map<String, SummarizedUser> = emptyMap(),
                var lastMessage: ChatMessage = ChatMessage("", "", 0L),
                val deliveredMessages: Map<String, Int> = emptyMap()) //TODO remove
    : PaperParcelable {

    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(Chat::class.java)
    }

}
