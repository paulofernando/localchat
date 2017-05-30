package site.paulo.localchat.ui.signin

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat.startActivity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import site.paulo.localchat.R
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.dashboard.DashboardActivity
import site.paulo.localchat.ui.signup.SignUpActivity
import javax.inject.Inject
import android.os.Looper
import org.jetbrains.anko.indeterminateProgressDialog


class SignInActivity : BaseActivity(), SignInContract.View {

    @Inject
    lateinit var presenter: SignInPresenter

    @BindView(R.id.input_email)
    lateinit var inputEmail: EditText

    @BindView(R.id.input_password)
    lateinit var inputPassword: EditText

    @BindView(R.id.btnLogin)
    lateinit var btnLogin: Button

    @BindView(R.id.link_signup)
    lateinit var linkSignUp: TextView

    var spinnerDialog: ProgressDialog? = null

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

        presenter.attachView(this)

        btnLogin.setOnClickListener {
            if(inputEmail.text.toString().equals("")) {
                inputEmail.error = "enter an email address"
            } else if(inputPassword.text.toString().equals("")) {
                inputPassword.error = "enter a password"
            } else {
                spinnerDialog = indeterminateProgressDialog(R.string.authenticating)
                presenter.signIn(inputEmail.text.toString(), inputPassword.text.toString())
            }
        }

        linkSignUp.setOnClickListener {
            startActivity<SignUpActivity>()
        }

        presenter.isAuthenticated() //if authenticated, sign in
    }

    override fun showSuccessFullSignIn() {
        spinnerDialog?.cancel()
        startActivity<DashboardActivity>()
    }

    override fun showFailSignIn() {
        spinnerDialog?.cancel()
        Handler(Looper.getMainLooper()).post({ alert(R.string.auth_failed){  }.show() })
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

}