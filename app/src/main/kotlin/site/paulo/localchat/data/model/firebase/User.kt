package site.paulo.localchat.data.model.firebase

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var name: String = "",
    var age: Long = 0L,
    var email: String = "",
    var gender: String = "",
    var pic: String = "",
    var acc: Long = 0L,
    var lat: Long = 0L,
    var lon: Long = 0L,
    var geohash: String = "",
    var chats: MutableMap<String, String> = mutableMapOf<String, String>())
    : Parcelable {

    @IgnoredOnParcel
    @Transient var userPicBitmap: Bitmap? = null

}