package site.paulo.localchat.test.common

import site.paulo.localchat.data.model.firebase.*
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

    @JvmStatic fun makeListNearbyUsers(number: Int): List<NearbyUser> {
        val users = ArrayList<NearbyUser>()
        for (i in 0..number.dec()) {
            users.add(makeNearbyUser(i.toString()))
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
            chats = mutableMapOf("a@a_com" to "cHjyfgh"))
    }

    @JvmStatic fun makeUser(uniqueSuffix: String, chatId: String): User {
        return User(
            name = "name$uniqueSuffix",
            age = 29,
            email = "email$uniqueSuffix@paulo.site",
            gender = "m",
            pic = "https://api.adorable.io/avatars/285/$uniqueSuffix@adorable.png",
            chats = mutableMapOf("a@a_com" to "cHjyfgh"))
    }

    @JvmStatic fun makeUser(uniqueSuffix: String, chatId: String, email: String): User {
        return User(
                name = "name$uniqueSuffix",
                age = 29,
                email = email,
                gender = "m",
                pic = "https://api.adorable.io/avatars/285/$uniqueSuffix@adorable.png",
                chats = mutableMapOf("a@a_com" to "cHjyfgh"))
    }

    @JvmStatic fun makeNearbyUser(uniqueSuffix: String): NearbyUser {
        return NearbyUser(
                name = "name$uniqueSuffix",
                age = 29,
                email = "email$uniqueSuffix@paulo.site",
                pic = "https://api.adorable.io/avatars/285/$uniqueSuffix@adorable.png",
                lastGeoUpdate = 0)
    }

    @JvmStatic fun makeUserEmptyChatList(uniqueSuffix: String): User {
        return User(
            name = "name$uniqueSuffix",
            age = 29,
            email = "email$uniqueSuffix@paulo.site",
            gender = "m",
            pic = "https://api.adorable.io/avatars/285/$uniqueSuffix@adorable.png",
            chats = mutableMapOf())
    }

    @JvmStatic fun makeChat(uniqueSuffix: String): Chat {
        return Chat(
            id = "id$uniqueSuffix",
            lastMessage = ChatMessage("", "", 0L),
            users = mapOf("abc" to makeSummarizedUser("s1")))
    }

    @JvmStatic fun makeEmptyChat(): Chat {
        return Chat(
                id = "",
                lastMessage = ChatMessage("", "", 0L),
                users = mapOf("abc" to makeSummarizedUser("s1")))
    }

    @JvmStatic fun makeChatMessage(): ChatMessage {
        return ChatMessage(
                owner = "p@p_com",
                message = "Test message",
                timestamp = 123456789L)
    }

    @JvmStatic fun makeEmptyChatMessage(): ChatMessage {
        return ChatMessage(
                owner = "p@p_com",
                message = "",
                timestamp = 123456789L)
    }

    @JvmStatic fun makeSummarizedUser(uniqueSuffix: String): SummarizedUser {
        return SummarizedUser(
            name = "name$uniqueSuffix",
            pic = "https://api.adorable.io/avatars/285/$uniqueSuffix@adorable.png")
    }

}
