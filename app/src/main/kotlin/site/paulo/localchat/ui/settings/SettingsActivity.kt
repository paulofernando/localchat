package site.paulo.localchat.ui.settings

import android.os.Bundle
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import site.paulo.localchat.R
import site.paulo.localchat.ui.base.BaseActivity

class SettingsActivity: BaseActivity() {

    @BindView(R.id.toolbarSettings)
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        setContentView(R.layout.activity_settings)
        ButterKnife.bind(this)

        toolbar.title = resources.getString(R.string.title_settings)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

    }


}
