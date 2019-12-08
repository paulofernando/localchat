package site.paulo.localchat.data.model.firebase

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chat(val id: String? = "",
                val users: Map<String, SummarizedUser> = emptyMap(),
                var lastMessage: ChatMessage = ChatMessage("", "", 0L),
                val deliveredMessages: Map<String, Int> = emptyMap()) //TODO remove
    : Parcelable { }
