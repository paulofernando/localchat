package site.paulo.localchat.data.model.firebase

import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class NearbyUser(
        val lastGeoUpdate: Long = 0,
        val name: String = "",
        val email: String = "",
        val age: Long = 0L,
        val pic: String = "") : PaperParcelable {

    companion object {
       @JvmField val CREATOR = PaperParcelable.Creator(NearbyUser::class.java)
    }
    
}