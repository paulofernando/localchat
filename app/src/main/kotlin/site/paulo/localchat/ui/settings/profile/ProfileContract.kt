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

package site.paulo.localchat.ui.settings.profile

import android.net.Uri
import com.google.firebase.database.DatabaseReference
import site.paulo.localchat.data.remote.FirebaseHelper
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object ProfileContract {

    interface View : MvpView {
        fun showCurrentUserData()

        fun editName(view: android.view.View)
        fun cancelNameEdition(view : android.view.View)
        fun confirmNameEdition(view : android.view.View)

        fun editAge(view: android.view.View)
        fun cancelAgeEdition(view : android.view.View)
        fun confirmAgeEdition(view : android.view.View)

        fun updatePic(url: String)
    }

    abstract class Presenter : BaseMvpPresenter<ProfileContract.View>() {
        abstract fun updateUserData(dataType: FirebaseHelper.Companion.UserDataType, value:String)
        abstract fun uploadPic(selectedImageUri: Uri)
    }

}