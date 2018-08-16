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
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_sign_up.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivity()

        createSignUpBtn.setOnClickListener {
            if(validate()) {
                spinnerDialog = indeterminateProgressDialog(R.string.registering)
                presenter.signUp(emailSignUpTxt.text.toString(), passwordSignUpTxt.text.toString(),
                        nameSignUpTxt.text.toString(), ageSignUpTxt.text.toString().toLong(), getSelectedGender())
            }
        }

        signinSignUpLink.setOnClickListener {
            finish()
        }

        val adapter = ArrayAdapter.createFromResource(this, R.array.genders, R.layout.spinner_item)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        genderSignUpSpinner.setAdapter(adapter)
    }

    private fun setupActivity() {
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

        val email = emailSignUpTxt.text.toString()
        val password = passwordSignUpTxt.text.toString()
        val name = nameSignUpTxt.text.toString()
        val age = if (ageSignUpTxt.text.isEmpty()) 0 else ageSignUpTxt.text.toString().toLong()

        if (name.isEmpty() || name.length < minimumCharsName || name.length > maximumCharsName) {
            nameSignUpTxt.error = resources.getString(R.string.error_chars_bet_numbers, minimumCharsName, maximumCharsName)
            valid = false
        } else {
            nameSignUpTxt.error = null
        }

        if (emailSignUpTxt.text.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailSignUpTxt.error = resources.getString(R.string.error_email_valid)
            valid = false
        } else {
            emailSignUpTxt.error = null
        }

        if (passwordSignUpTxt.text.isEmpty() || password.length < minimumCharsPassword || password.length > maximumCharsPassword) {
            passwordSignUpTxt.error = resources.getString(R.string.error_chars_bet_numbers, minimumCharsPassword, maximumCharsPassword)
            valid = false
        } else {
            passwordSignUpTxt.error = null
        }

        if (ageSignUpTxt.text.isEmpty() || age !in (minimumAge - 1) .. (maximumAge + 1)) {
            ageSignUpTxt.error = resources.getString(R.string.error_numbers_bet, minimumAge, maximumAge)
            valid = false
        } else {
            ageSignUpTxt.error = null
        }

        if(getSelectedGender() == ("")) {
            (genderSignUpSpinner.selectedView as TextView).error = resources.getString(R.string.error_gender)
            valid = false
        } else {
            (genderSignUpSpinner.selectedView as TextView).error = null
        }

        return valid
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    private fun getSelectedGender(): String {
        when((genderSignUpSpinner.selectedView as TextView).text.toString()) {
            resources.getString(R.string.spinner_gender_female) -> return "f"
            resources.getString(R.string.spinner_gender_male) -> return "m"
            resources.getString(R.string.spinner_gender_non_binary) -> return "n"
        }
        return ""
    }

}