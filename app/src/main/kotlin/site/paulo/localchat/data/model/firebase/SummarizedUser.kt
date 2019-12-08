package site.paulo.localchat.data.model.firebase

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/* Used in the duplicated data of chats */
@Parcelize
data class SummarizedUser(val name: String = "", val pic: String = "") : Parcelable