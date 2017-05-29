package site.paulo.localchat.data.model.chatgeo

data class User(
    val name: String = "",
    val chats: Map<String, Boolean> = emptyMap(),
    val age: Long = 0L,
    val email: String = "",
    val gender: String = "",
    val profilePic: String = "")