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
import android.os.Handler
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.fragment_dashboard.*
import site.paulo.localchat.R
import site.paulo.localchat.data.model.firebase.NearbyUser
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
    lateinit var usersNearbyList: androidx.recyclerview.widget.RecyclerView

    @BindView(R.id.usersNearbySwipeLayout)
    lateinit var usersNearbySwipeLayout: SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = setupFragment(inflater, container)

        usersNearbyList.adapter = usersAdapter
        usersNearbyList.layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, 3)

        presenter.loadUsers()

        usersNearbySwipeLayout.setOnRefreshListener(object: SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                presenter.loadUsers({usersNearbySwipeLayout.setRefreshing(false)})
            }

        })

        return rootView
    }

    private fun setupFragment(inflater: LayoutInflater, container: ViewGroup?): View {
        activityComponent.inject(this)
        presenter.attachView(this)
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun showNearbyUsers(nearbyUser: List<NearbyUser>) {
        usersAdapter.nearbyUsers = nearbyUser.toMutableList()
        usersAdapter.notifyDataSetChanged()
    }

    override fun showNearbyUser(nearbyUser: NearbyUser) {
        val foundUser = usersAdapter.nearbyUsers.any { it.email.equals(nearbyUser.email) }
        if(!foundUser) {
            usersAdapter.nearbyUsers.add(nearbyUser)
            usersAdapter.notifyDataSetChanged()
        }
    }

    override fun removeNearbyUser(nearbyUser: NearbyUser) {
        val foundUsers = usersAdapter.nearbyUsers.filter { it.email.equals(nearbyUser.email) }
        if(!foundUsers.isEmpty()) {
            usersAdapter.nearbyUsers.removeAll(foundUsers)
            usersAdapter.notifyDataSetChanged()
        }
    }

    override fun showNearbyUsersEmpty() {
        usersAdapter.nearbyUsers = mutableListOf<NearbyUser>()
        usersAdapter.notifyDataSetChanged()
        Toast.makeText(activity, R.string.empty_nearby_users, Toast.LENGTH_LONG).show()
    }

    override fun showError() {
        Toast.makeText(activity, R.string.error_loading_nearby_users, Toast.LENGTH_LONG).show()
    }

}