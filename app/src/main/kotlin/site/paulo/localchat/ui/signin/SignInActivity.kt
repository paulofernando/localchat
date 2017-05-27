package site.paulo.localchat.ui.signin

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import org.jetbrains.anko.startActivity
import site.paulo.localchat.R
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.dashboard.DashboardActivity
import site.paulo.localchat.ui.signup.SignUpActivity
import javax.inject.Inject

class SignInActivity : BaseActivity(), SignInContract.View {

    @Inject
    lateinit var presenter: SignInPresenter

    @BindView(R.id.btnLogin)
    lateinit var btnLogin: Button

    @BindView(R.id.link_signup)
    lateinit var linkSignUp: TextView

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

        setContentView(R.layout.activity_sign_in)
        ButterKnife.bind(this)

        presenter.isAuthenticated() //if authenticated, sign in
        presenter.attachView(this)

        btnLogin.setOnClickListener {
            showSuccessFullSignIn()
        }

        linkSignUp.setOnClickListener {
            startActivity<SignUpActivity>()
        }
    }

    override fun showSuccessFullSignIn() {
        startActivity<DashboardActivity>()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

}