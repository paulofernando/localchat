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
object CurrentUserManager {

    private var user: User? = null

    fun getUser(): User {
        return user!!
    }

    fun getUserId(): String {
        return Utils.getFirebaseId(user?.email ?: "")
    }

    fun setUser(user: User?) {
        if (user != null) this.user = user
    }

    fun setGeohash(geohash: String) {
        if (user != null) user!!.geohash = geohash
        Timber.d("Current user data updated: %s", user.toString())
    }

    fun setUserName(name: String) {
        if (user != null) user!!.name = name
        Timber.d("Current user data updated: %s", user.toString())
    }

    fun setAge(age: Long) {
        if (user != null) user!!.age = age
        Timber.d("Current user data updated: %s", user.toString())
    }

    fun setPic(url: String) {
        if (user != null) user!!.pic = url
        Timber.d("Current user data updated: %s", user.toString())
    }

    fun setPic(bmp: Bitmap) {
        user?.userPicBitmap = bmp
        Timber.d("Current user pic (bitmap) updated")
    }

}

