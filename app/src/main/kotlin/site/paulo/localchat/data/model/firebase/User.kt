package site.paulo.localchat.data.model.firebase

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val name: String = "",
    val age: Long = 0L,
    val email: String = "",
    val gender: String = "",
    val pic: String = "",
    val acc: Long = 0L,
    val lat: Long = 0L,
    val lon: Long = 0L,
    val geohash: String = "",
    val chats: MutableMap<String, String> = mutableMapOf<String, String>())
    : Parcelable {

    @IgnoredOnParcel
    @Transient var userPicBitmap: Bitmap? = null

}