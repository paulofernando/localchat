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

package site.paulo.localchat.ui.signup

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import org.jetbrains.anko.alert
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity
import site.paulo.localchat.R
import site.paulo.localchat.R.drawable.gender
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.dashboard.DashboardActivity
import javax.inject.Inject
import android.widget.ArrayAdapter
import timber.log.Timber
import java.util.ArrayList


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

    @BindView(R.id.genderSignUpSpinner)
    lateinit var inGender: Spinner

    @BindView(R.id.createSignUpBtn)
    lateinit var btnCreate: Button

    @BindView(R.id.signinSignUpLink)
    lateinit var linkSignIn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        presenter.attachView(this)

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

        btnCreate.setOnClickListener {
            if(validate()) {
                spinnerDialog = indeterminateProgressDialog(R.string.registering)
                presenter.signUp(inEmail.text.toString(), inPassword.text.toString(),
                    inName.text.toString(), inAge.text.toString().toLong(), getSelectedGender())
            }
        }

        linkSignIn.setOnClickListener {
            finish()
        }

        val adapter = ArrayAdapter.createFromResource(this, R.array.genders, R.layout.spinner_item)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        inGender.setAdapter(adapter)
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

        val minimumCharsName: Int = resources.getString(R.string.number_minimum_chars_user_name).toInt()
        val maximumCharsName: Int = resources.getString(R.string.number_maximum_chars_user_name).toInt()
        val minimumCharsPassword: Int = resources.getString(R.string.number_minimum_chars_password).toInt()
        val maximumCharsPassword: Int = resources.getString(R.string.number_maximum_chars_password).toInt()
        val minimumAge: Int = resources.getString(R.string.number_minimum_age).toInt()
        val maximumAge: Int = resources.getString(R.string.number_maximum_age).toInt()

        val email = inEmail.text.toString()
        val password = inPassword.text.toString()
        val name = inName.text.toString()
        val age = if (inAge.text.isEmpty()) 0 else inAge.text.toString().toLong()

        if (name.isEmpty() || name.length < minimumCharsName || name.length > maximumCharsName) {
            inName.error = resources.getString(R.string.error_chars_bet_numbers, minimumCharsName, maximumCharsName)
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

        if (inPassword.text.isEmpty() || password.length < minimumCharsPassword || password.length > maximumCharsPassword) {
            inPassword.error = resources.getString(R.string.error_chars_bet_numbers, minimumCharsPassword, maximumCharsPassword)
            valid = false
        } else {
            inPassword.error = null
        }

        if (inAge.text.isEmpty() || age !in (minimumAge - 1) .. (maximumAge + 1)) {
            inAge.error = resources.getString(R.string.error_numbers_bet, minimumAge, maximumAge)
            valid = false
        } else {
            inAge.error = null
        }

        if(getSelectedGender().equals((""))) {
            (inGender.selectedView as TextView).error = resources.getString(R.string.error_gender)
            valid = false
        } else {
            (inGender.selectedView as TextView).error = null
        }

        return valid
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    private fun getSelectedGender(): String {
        when((inGender.selectedView as TextView).text.toString()) {
            resources.getString(R.string.spinner_gender_female) -> return "f"
            resources.getString(R.string.spinner_gender_male) -> return "m"
            resources.getString(R.string.spinner_gender_non_binary) -> return "n"
        }
        return ""
    }

}