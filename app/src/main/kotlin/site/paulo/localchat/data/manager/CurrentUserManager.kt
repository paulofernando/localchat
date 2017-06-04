package site.paulo.localchat.data.manager

import site.paulo.localchat.data.model.chatgeo.User
import javax.inject.Singleton

@Singleton
class CurrentUserManager {

    private object Holder { val INSTANCE = CurrentUserManager() }

    private var user:User? = null

    companion object {
        val instance: CurrentUserManager by lazy { Holder.INSTANCE }
    }

    fun getUser(): User {
        return user!!
    }

    fun setUser(user:User){
        this.user = user
    }

}

