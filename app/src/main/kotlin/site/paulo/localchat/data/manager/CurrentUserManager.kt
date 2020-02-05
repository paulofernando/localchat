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

import site.paulo.localchat.data.LocalDataManager
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.ui.utils.Utils
import site.paulo.localchat.ui.utils.getFirebaseId
import javax.inject.Singleton

@Singleton
class CurrentUserManager {

    private val CURRENT_USER = "currentUser"
    private var user: User? = null

    private object HOLDER {
        val INSTANCE = CurrentUserManager()
    }

    companion object {
        val instance: CurrentUserManager by lazy { HOLDER.INSTANCE }
    }

    lateinit var localDataManager: LocalDataManager

    fun setUser(user: User, localDataManager: LocalDataManager) {
        this.user = user
        this.localDataManager = localDataManager
        localDataManager.put(CURRENT_USER, user)
    }

    /**
     * Update current user based on firebaseAuth email, if user is already authenticated
     */
    fun setUserByEmail(email: String?, localDataManager: LocalDataManager) {
        if (email == null) return
        this.localDataManager = localDataManager
        if (localDataManager.contains(CURRENT_USER)) {
            val user = localDataManager.get(CURRENT_USER, User::class.java)
            if (user.email == email) this.user = user
        }
    }

    fun getUser(): User? {
        if (!hasUser()) { //user still not set, get the last successfully stored one
            if (localDataManager.contains(CURRENT_USER)) {
                this.user = localDataManager.get(CURRENT_USER, User::class.java)
            }
        }
        return user
    }

    fun getUserId(): String {
        return Utils.getFirebaseId(user?.email ?: "")
    }

    fun hasUser(): Boolean {
        return this.user != null
    }

}

