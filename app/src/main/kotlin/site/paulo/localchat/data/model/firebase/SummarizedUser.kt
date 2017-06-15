package site.paulo.localchat.data.model.firebase

import nz.bradcampbell.paperparcel.PaperParcel

/* Used in the duplicated data of chats */
@PaperParcel
data class SummarizedUser(val name: String = "", val pic: String = "")