package site.paulo.localchat.data.manager

import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.ui.utils.Utils
import site.paulo.localchat.ui.utils.getFirebaseId
import javax.inject.Singleton

@Singleton
class CurrentUserManager {

    private object Holder { val INSTANCE = CurrentUserManager() }

    private var user:User? = null

    companion object {
        val instance: CurrentUserManager by lazy { Holder.INSTANCE }
    }

    fun getUser(): User { //TODO Increase the quality of nullability code
        return user!!
    }

    fun getUserId(): String {
        return Utils.getFirebaseId(user!!.email)
    }

    fun setUser(user:User){
        this.user = user
    }

}

