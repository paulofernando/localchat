package site.paulo.localchat.ui.user

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import site.paulo.localchat.R
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.user.UsersAdapter
import javax.inject.Inject

class UserActivity : BaseActivity(), UserContract.View {

    @Inject
    lateinit var presenter: UserPresenter

    @Inject
    lateinit var usersAdapter: UsersAdapter

    @BindView(R.id.forecastList)
    lateinit var forecastList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        setContentView(R.layout.activity_forecast)
        ButterKnife.bind(this)

        forecastList.adapter = usersAdapter
        forecastList.layoutManager = LinearLayoutManager(this)

        presenter.attachView(this)
        presenter.loadUsers()

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun showUsers(users: List<User>) {
        usersAdapter.users = users
        usersAdapter.notifyDataSetChanged()
    }

    override fun showUsersEmpty() {
        usersAdapter.users = emptyList()
        usersAdapter.notifyDataSetChanged()
        Toast.makeText(this, R.string.empty_ribots, Toast.LENGTH_LONG).show()
    }

    override fun showError() {
        Toast.makeText(this, R.string.error_loading_ribots, Toast.LENGTH_LONG).show()
    }

}