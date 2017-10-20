/*
 * Copyright 2017 Paulo Fernando
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        return Utils.getFirebaseId(user?.email ?: "")
    }

    fun setUser(user:User){
        this.user = user
    }

    fun setUserName(name: String){
        this.user = User(name, user!!.age, user!!.email, user!!.gender, user!!.pic, user!!.chats)
        Timber.i("Current user data updated: " + user.toString())
    }

    fun setAge(age: Long){
        this.user = User(user?.name ?: "", age, user?.email ?: "", user?.gender ?: "", user?.pic ?: "", user!!.chats)
        Timber.i("Current user data updated: " + user.toString())
    }

    fun setPic(url: String){
        this.user = User(user?.name ?: "", user?.age ?: 0, user?.email ?: "", user?.gender ?: "", url, user!!.chats)
        Timber.i("Current user data updated: " + user.toString())
    }

    fun setPic(bmp: Bitmap){
        this.user?.userPicBitmap = bmp
        Timber.i("Current user pic (bitmap) updated")
    }

}

