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

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import site.paulo.localchat.R
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.ui.base.BaseFragment
import site.paulo.localchat.ui.user.UserNearbyPresenter
import site.paulo.localchat.ui.user.UsersNearbyAdapter
import javax.inject.Inject

class UsersNearbyFragment : BaseFragment(), UsersNearbyContract.View {

    @Inject
    lateinit var presenter: UserNearbyPresenter

    @Inject
    lateinit var usersAdapter: UsersNearbyAdapter

    @BindView(R.id.usersNearbyList)
    lateinit var usersNearbyList: RecyclerView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        activityComponent.inject(this)
        presenter.attachView(this)
        val rootView = inflater!!.inflate(R.layout.fragment_dashboard, container, false)
        ButterKnife.bind(this, rootView)

        usersNearbyList.adapter = usersAdapter
        usersNearbyList.layoutManager = GridLayoutManager(activity, 3)

        presenter.loadNearbyUsers()
        presenter.listenNearbyUsers()

        return rootView
    }

    override fun showNearbyUsers(users: List<User>) {
        usersAdapter.users = users.toMutableList()
        usersAdapter.notifyDataSetChanged()
    }

    override fun showNearbyUser(user: User) {
        usersAdapter.users.add(user)
        usersAdapter.notifyDataSetChanged()
    }

    override fun showNearbyUsersEmpty() {
        usersAdapter.users = mutableListOf<User>()
        usersAdapter.notifyDataSetChanged()
        Toast.makeText(activity, R.string.empty_nearby_users, Toast.LENGTH_LONG).show()
    }

    override fun showError() {
        Toast.makeText(activity, R.string.error_loading_nearby_users, Toast.LENGTH_LONG).show()
    }

}