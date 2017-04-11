package site.paulo.localchat.data.model.chatgeo

import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class Chat(val name: String = "",
    val chatPic: String = "",
    val users: Map<String, SummarizedUser> = emptyMap())//UserId, Username //TODO every time the na me was change, change in this part of DB too
    : PaperParcelable {

    companion object {
       @JvmField val CREATOR = PaperParcelable.Creator(Chat::class.java)
    }

}
