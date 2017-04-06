package site.paulo.localchat.data.model.chatgeo

import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class Chat(val name: String = "", val chatPic: String = "") : PaperParcelable {
    companion object {
       @JvmField val CREATOR = PaperParcelable.Creator(Chat::class.java)
    }
}
