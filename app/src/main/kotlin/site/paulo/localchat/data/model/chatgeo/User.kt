package site.paulo.localchat.data.model.chatgeo

data class User(
    val name: String = "",
    val chats: Map<String, Boolean> = emptyMap(),
    val age: Int = 0,
    val email: String = "",
    val gender: String = "",
    val profilePic: String = "")