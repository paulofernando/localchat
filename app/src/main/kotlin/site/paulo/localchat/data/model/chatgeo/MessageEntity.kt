package site.paulo.localchat.data.model.chatgeo

data class MessageEntity(val id: String,
    val senderId: String,
    val receiverId: String,
    val text: String,
    val timestamp: Long)