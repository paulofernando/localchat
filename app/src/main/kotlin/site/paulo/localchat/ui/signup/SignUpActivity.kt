package site.paulo.localchat.ui.signup

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import org.jetbrains.anko.startActivity
import site.paulo.localchat.R
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.dashboard.DashboardActivity
import javax.inject.Inject

class SignUpActivity : BaseActivity(), SignUpContract.View {

    @Inject
    lateinit var presenter: SignUpPresenter

    @BindView(R.id.btnLogin)
    lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)

        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
        }

        setContentView(R.layout.activity_sign_up)
        ButterKnife.bind(this)

        presenter.attachView(this)

        btnLogin.setOnClickListener {
            startActivity<DashboardActivity>()
        }
    }

    override fun showSuccessFullSignUp() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

}