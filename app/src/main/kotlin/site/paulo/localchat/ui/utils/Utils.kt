package site.paulo.localchat.ui.utils

class Utils {
    companion object {}
}

fun Utils.Companion.getCurrentUserId(): String {
    return "kGbfdjuhsug"
}

//TODO Temp
fun Utils.Companion.isMe(id: String): Boolean {
    return id.equals("kGbfdjuhsug")
}