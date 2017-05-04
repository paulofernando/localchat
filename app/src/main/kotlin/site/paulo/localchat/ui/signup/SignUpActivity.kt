package site.paulo.localchat.ui.signup

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

    @BindView(R.id.input_email_su)
    lateinit var inEmail: EditText

    @BindView(R.id.input_password_su)
    lateinit var inPassword: EditText

    @BindView(R.id.input_name_su)
    lateinit var inName: EditText

    @BindView(R.id.input_age_su)
    lateinit var inAge: EditText

    @BindView(R.id.input_gender_su)
    lateinit var inGender: EditText

    @BindView(R.id.btnCreate)
    lateinit var btnCreate: Button

    @BindView(R.id.link_signin)
    lateinit var linkSignIn: TextView

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

        btnCreate.setOnClickListener {
            validate()
        }

        linkSignIn.setOnClickListener {
            finish()
        }
    }

    override fun showSuccessFullSignUp() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validate(): Boolean {
        var valid = true

        val email = inEmail.text.toString()
        val password = inPassword.text.toString()
        val name = inName.text.toString()
        val age = inAge.text.toString()
        val gender = inGender.text.toString()

        if (name.isEmpty() || name.length < 6) {
            inName.setError("at least 6 characters")
            valid = false
        } else {
            inName.setError(null)
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inEmail.setError("enter a valid email address")
            valid = false
        } else {
            inEmail.setError(null)
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            inPassword.setError("between 4 and 10 alphanumeric characters")
            valid = false
        } else {
            inPassword.setError(null)
        }

        if (age.isEmpty() || !(age.toInt() in 100..17)) {
            inAge.setError("between 18 and 99")
            valid = false
        } else {
            inAge.setError(null)
        }

        if (gender.isEmpty() || !gender.equals("m") && !gender.equals("f")) {
            inGender.setError("f or m")
            valid = false
        } else {
            inGender.setError(null)
        }

        return valid
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

}