package site.paulo.localchat.data.model.firebase

import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class User(
    val name: String = "",
    val age: Long = 0L,
    val email: String = "",
    val gender: String = "",
    val pic: String = "",
    val chats: Map<String, String> = emptyMap())
    : PaperParcelable {

    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(User::class.java)
    }

}