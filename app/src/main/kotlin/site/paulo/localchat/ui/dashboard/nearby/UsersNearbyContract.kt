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

package site.paulo.localchat.ui.dashboard.nearby

import site.paulo.localchat.data.model.firebase.NearbyUser
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object UsersNearbyContract {

    interface View : MvpView {
        fun showNearbyUsers(nearbyUser: List<NearbyUser>)
        fun showNearbyUser(nearbyUser: NearbyUser)
        fun removeNearbyUser(nearbyUser: NearbyUser)
        fun showNearbyUsersEmpty()
        fun showError()
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun loadUsers(callback: (() -> Unit)? = null)
        abstract fun listenNearbyUsers()
    }
}
