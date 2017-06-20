package site.paulo.localchat.data.manager

import android.graphics.Bitmap
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.ui.utils.Utils
import site.paulo.localchat.ui.utils.getFirebaseId
import timber.log.Timber
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

    fun setUserName(name: String){
        this.user = User(name, user!!.age, user!!.email, user!!.gender, user!!.pic, user!!.chats)
        Timber.i("Current user data updated: " + user.toString())
    }

    fun setAge(age: Long){
        this.user = User(user!!.name, age, user!!.email, user!!.gender, user!!.pic, user!!.chats)
        Timber.i("Current user data updated: " + user.toString())
    }

    fun setPic(url: String){
        this.user = User(user!!.name, user!!.age, user!!.email, user!!.gender, url, user!!.chats)
        Timber.i("Current user data updated: " + user.toString())
    }

    /*fun setPic(bmp: Bitmap){
        this.user?.userPicBitmap = bmp
        Timber.i("Current user pic (bitmap) updated")
    }*/

}

