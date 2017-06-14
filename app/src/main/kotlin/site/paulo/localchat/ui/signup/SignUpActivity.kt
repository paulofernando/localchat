package site.paulo.localchat.ui.signup

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import org.jetbrains.anko.alert
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity
import site.paulo.localchat.R
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.dashboard.DashboardActivity
import javax.inject.Inject

class SignUpActivity : BaseActivity(), SignUpContract.View {

    @Inject
    lateinit var presenter: SignUpPresenter

    @BindView(R.id.emailSignUpTxt)
    lateinit var inEmail: EditText

    @BindView(R.id.passwordSignUpTxt)
    lateinit var inPassword: EditText

    @BindView(R.id.nameSignUpTxt)
    lateinit var inName: EditText

    @BindView(R.id.ageSignUpTxt)
    lateinit var inAge: EditText

    @BindView(R.id.genderSignUpTxt)
    lateinit var inGender: EditText

    @BindView(R.id.createSignUpBtn)
    lateinit var btnCreate: Button

    @BindView(R.id.signinSignUpLink)
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
            if(validate()) {
                spinnerDialog = indeterminateProgressDialog(R.string.authenticating)
                presenter.signUp(inEmail.text.toString(), inPassword.text.toString(),
                    inName.text.toString(), inAge.text.toString().toLong(), inGender.text.toString())
            }
        }

        linkSignIn.setOnClickListener {
            finish()
        }
    }

    override fun showSuccessFullSignUp() {
        spinnerDialog?.cancel()
        startActivity<DashboardActivity>()
    }

    override fun showFailSignUp() {
        spinnerDialog?.cancel()
        Handler(Looper.getMainLooper()).post({ alert(R.string.registration_failed){  }.show() })
    }

    override fun validate(): Boolean {
        var valid = true

        val email = inEmail.text.toString()
        val password = inPassword.text.toString()
        val name = inName.text.toString()
        val age = inAge.text.toString().toLong()
        val gender = inGender.text.toString()

        if (name.isEmpty() || name.length < 6) {
            inName.error = resources.getString(R.string.error_chars_least_number)
            valid = false
        } else {
            inName.error = null
        }

        if (inEmail.text.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inEmail.error = resources.getString(R.string.error_email_valid)
            valid = false
        } else {
            inEmail.error = null
        }

        if (inPassword.text.isEmpty() || password.length < 4 || password.length > 10) {
            inPassword.error = resources.getString(R.string.error_chars_bet_numbers)
            valid = false
        } else {
            inPassword.error = null
        }

        if (inAge.text.isEmpty() || age !in 17..100) {
            inAge.error = resources.getString(R.string.error_age_bet_numbers)
            valid = false
        } else {
            inAge.error = null
        }

        if (gender.isEmpty() || !gender.equals("m") && !gender.equals("f")) {
            inGender.error = resources.getString(R.string.error_gender)
            valid = false
        } else {
            inGender.error = null
        }

        return valid
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

}