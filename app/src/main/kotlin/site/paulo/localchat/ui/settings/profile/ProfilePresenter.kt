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

import android.net.Uri
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.remote.FirebaseHelper
import site.paulo.localchat.exception.MissingCurrentUserException
import site.paulo.localchat.ui.settings.profile.ProfileContract
import timber.log.Timber
import javax.inject.Inject


class ProfilePresenter
@Inject
constructor(private val dataManager: DataManager,
    private val currentUserManager: CurrentUserManager,
    private val firebaseStorage: FirebaseStorage) : ProfileContract.Presenter() {

    override fun updateUserData(dataType: FirebaseHelper.Companion.UserDataType, value:String) {
        val completionListener = DatabaseReference.CompletionListener { _, _ ->
            val currentUser = currentUserManager.getUser() ?: throw MissingCurrentUserException("No user set as current")
            Timber.i("User data updated")
            when(dataType) {
                FirebaseHelper.Companion.UserDataType.NAME -> currentUser.name = value
                FirebaseHelper.Companion.UserDataType.AGE -> currentUser.age = value.toLong()
                FirebaseHelper.Companion.UserDataType.PIC -> currentUser.pic = value
            }
        }
        dataManager.updateUserData(dataType, value, completionListener)

    }

    override fun uploadPic(selectedImageUri: Uri) {
        // Get a reference to the location where we'll store our photos
        var storageRef = firebaseStorage.getReference("chat_pics")
        val path = selectedImageUri.lastPathSegment ?: return
        // Get a reference to store file at chat_photos/<FILENAME>
        val photoRef = storageRef.child(path)

        // Upload file to Firebase Storage
        photoRef.putFile(selectedImageUri).addOnSuccessListener { taskSnapshot ->
            Timber.i("Image sent successfully!")
            val downloadUrl = taskSnapshot.storage.downloadUrl
            updateUserData(FirebaseHelper.Companion.UserDataType.PIC, downloadUrl.toString())
            view.updatePic(downloadUrl.toString())
        }
    }

}