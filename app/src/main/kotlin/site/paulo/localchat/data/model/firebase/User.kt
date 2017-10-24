package site.paulo.localchat.data.model.firebase

import android.graphics.Bitmap
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class User(
    val name: String = "",
    val age: Long = 0L,
    val email: String = "",
    val gender: String = "",
    val pic: String = "",
    val acc: Long = 0L,
    val lat: Long = 0L,
    val lon: Long = 0L,
    val chats: MutableMap<String, String> = mutableMapOf<String, String>())
    : PaperParcelable {

    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(User::class.java)
    }

    @Transient var userPicBitmap: Bitmap? = null

}