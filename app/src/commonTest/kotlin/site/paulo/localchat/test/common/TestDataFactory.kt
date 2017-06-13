package site.paulo.localchat.test.common

import site.paulo.localchat.data.model.chatgeo.Chat
import site.paulo.localchat.data.model.chatgeo.SummarizedUser
import site.paulo.localchat.data.model.chatgeo.User
import java.util.ArrayList
import java.util.UUID

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
            pic = "https://api.adorable.io/avatars/285/$uniqueSuffix@adorable.png",
            chats = mapOf("c1" to true))
    }

    @JvmStatic fun makeChat(uniqueSuffix: String): Chat {
        return Chat(
            id = "id$uniqueSuffix",
            lastMessage = "lastMessage",
            users = mapOf("abc" to makeSummarizedUser("s1")))
    }

    @JvmStatic fun makeSummarizedUser(uniqueSuffix: String): SummarizedUser {
        return SummarizedUser(
            name = "name$uniqueSuffix",
            pic = "https://api.adorable.io/avatars/285/$uniqueSuffix@adorable.png")
    }

}
