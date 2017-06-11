package site.paulo.localchat.ui.utils

class Utils {
    companion object {}
}

fun Utils.Companion.getFirebaseId(email:String): String {
    return email.replace(".", "_", false)
}