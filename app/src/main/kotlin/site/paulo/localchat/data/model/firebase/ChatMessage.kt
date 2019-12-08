package site.paulo.localchat.data.model.firebase

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatMessage(val owner: String = "", val message: String = "",
    val timestamp: Long = 0L) : Parcelable { }