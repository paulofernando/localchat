package site.paulo.localchat.data.model.firebase

import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class Chat(val id: String = "",
    val users: Map<String, SummarizedUser> = emptyMap(),
    val lastMessage: String = "")
    : PaperParcelable {

    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(Chat::class.java)
    }

}
