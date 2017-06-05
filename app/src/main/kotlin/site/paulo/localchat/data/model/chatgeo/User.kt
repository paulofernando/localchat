package site.paulo.localchat.data.model.chatgeo

import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class User(
    val name: String = "",
    val chats: Map<String, Boolean> = emptyMap(),
    val age: Long = 0L,
    val email: String = "",
    val gender: String = "",
    val profilePic: String = "")
    : PaperParcelable {

    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(User::class.java)
    }

}