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

package site.paulo.localchat.ui.signin

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.*
import site.paulo.localchat.R
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.dashboard.DashboardActivity
import site.paulo.localchat.ui.signup.SignUpActivity
import javax.inject.Inject


class SignInActivity : BaseActivity(), SignInContract.View {

    @Inject
    lateinit var presenter: SignInPresenter

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
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        setContentView(R.layout.activity_sign_in)

        loginSignInBtn.setOnClickListener {
            if(validate()) presenter.signIn(emailSignInTxt.text.toString(), passwordSignInTxt.text.toString())
        }

        passwordSignInTxt.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            // If the event is a key-down event on the "enter" button
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Perform action on key press
                if(validate()) presenter.signIn(emailSignInTxt.text.toString(), passwordSignInTxt.text.toString())
                return@OnKeyListener true
            }
            false
        })

        signupSignInLink.setOnClickListener {
            startActivity<SignUpActivity>()
        }

        presenter.isAuthenticated() //if authenticated, sign in
    }

    fun validate(): Boolean {
        if(emailSignInTxt.text.toString().equals("")) {
            emailSignInTxt.error = "enter an email address"
            return false
        } else if(passwordSignInTxt.text.toString().equals("")) {
            passwordSignInTxt.error = "enter a password"
            return false
        } else {
            if(spinnerDialog == null) {
                spinnerDialog = indeterminateProgressDialog(R.string.authenticating)
            } else {
                spinnerDialog?.show()
            }
            return true
        }
    }

    override fun showSuccessFullSignIn() {
        spinnerDialog?.cancel()
        startActivity(intentFor<DashboardActivity>().newTask().clearTask())
    }

    override fun showFailSignIn() {
        spinnerDialog?.cancel()

        alert(R.string.auth_failed) {
            title = R.string.auth_failed_title.toString()
        }.show()

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

}