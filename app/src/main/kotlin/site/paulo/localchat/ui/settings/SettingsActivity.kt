package site.paulo.localchat.ui.settings

import android.os.Bundle
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import site.paulo.localchat.R
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.ui.base.BaseActivity
import javax.inject.Inject

class SettingsActivity: BaseActivity(), SettingsContract.View   {

    @BindView(R.id.toolbarSettings)
    lateinit var toolbar: Toolbar

    @Inject
    lateinit var presenter: SettingsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        setContentView(R.layout.activity_settings)
        ButterKnife.bind(this)

        presenter.attachView(this)

        toolbar.title = resources.getString(R.string.title_settings)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        presenter.loadCurrentUser()
    }

    override fun showCurrentUserData(user: User) {
        println(user.name)
    }


}
