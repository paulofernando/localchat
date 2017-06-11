package site.paulo.localchat.test.common

import site.paulo.localchat.data.model.chatgeo.User
import java.util.*

object TestDataFactory {

    @JvmStatic fun randomUuid(): String {
        return UUID.randomUUID().toString()
    }

    @JvmStatic fun makeListUsers(number: Int): List<User> {
        val users = ArrayList<User>()
        for (i in 0..number.dec()) {
            users.add(makeUser(i.toString()))
        }
        return users
    }

    @JvmStatic fun makeUser(uniqueSuffix: String): User {
        return User(
            name = "name$uniqueSuffix",
            age = 29,
            email = "email$uniqueSuffix@paulo.site",
            gender = "m",
            profilePic = "https://api.adorable.io/avatars/285/$uniqueSuffix@adorable.png",
            chats = mapOf("abc" to true))
    }

}
