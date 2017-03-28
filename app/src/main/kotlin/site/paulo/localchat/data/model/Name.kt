package site.paulo.localchat.data.model

import nz.bradcampbell.paperparcel.PaperParcel

@PaperParcel
data class Name(
        val first: String,
        val last: String)
