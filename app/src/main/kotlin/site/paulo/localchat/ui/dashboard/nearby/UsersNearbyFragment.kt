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
import site.paulo.localchat.data.model.chatgeo.User
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
        val rootView = inflater!!.inflate(R.layout.fragment_dashboard, container, false)
        ButterKnife.bind(this, rootView)

        usersNearbyList.adapter = usersAdapter
        usersNearbyList.layoutManager = GridLayoutManager(activity, 2)

        presenter.attachView(this)
        presenter.loadNearbyUsers()

        return rootView
    }

    override fun showNearbyUsers(users: List<User>) {
        usersAdapter.users = users
        usersAdapter.notifyDataSetChanged()
    }

    override fun showNearbyUsersEmpty() {
        usersAdapter.users = emptyList()
        usersAdapter.notifyDataSetChanged()
        Toast.makeText(activity, R.string.empty_nearby_users, Toast.LENGTH_LONG).show()
    }

    override fun showError() {
        Toast.makeText(activity, R.string.error_loading_nearby_users, Toast.LENGTH_LONG).show()
    }

}