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

package site.paulo.localchat.ui.settings

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.User
import timber.log.Timber
import javax.inject.Inject

class SettingsPresenter
@Inject
constructor(private val currentUserManager: CurrentUserManager,
    private val dataManager: DataManager) : SettingsContract.Presenter() {

    override fun loadCurrentUser() {
        view.showCurrentUserData(currentUserManager.getUser())
    }

    override fun registerProfileListener() {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {
                Timber.i("registerProfileListener:", dataSnapshot.getValue(User::class.java))
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }

        dataManager.registerUserChildEventListener(childEventListener)
    }

}