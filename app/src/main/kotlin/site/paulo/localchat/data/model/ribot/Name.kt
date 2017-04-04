package site.paulo.localchat.data.model.ribot

import nz.bradcampbell.paperparcel.PaperParcel

@PaperParcel
data class Name(
    val first: String,
    val last: String)
