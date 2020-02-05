package site.paulo.localchat.data.model.firebase

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NearbyUser(
        val lastGeoUpdate: Long = 0,
        val name: String = "",
        val email: String = "",
        val age: Long = 0L,
        val pic: String = "") : Parcelable {

    fun getSummarizedUser(): SummarizedUser {
        return SummarizedUser(this.name, this.pic)
    }
}