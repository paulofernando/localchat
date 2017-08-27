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
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import org.jetbrains.anko.AlertDialogBuilder
import org.jetbrains.anko.alert
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity
import site.paulo.localchat.R
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.dashboard.DashboardActivity
import site.paulo.localchat.ui.signup.SignUpActivity
import javax.inject.Inject
import android.content.Intent
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask


class SignInActivity : BaseActivity(), SignInContract.View {

    @Inject
    lateinit var presenter: SignInPresenter

    @BindView(R.id.emailSignInTxt)
    lateinit var inputEmail: EditText

    @BindView(R.id.passwordSignInTxt)
    lateinit var inputPassword: EditText

    @BindView(R.id.loginSignInBtn)
    lateinit var btnLogin: Button

    @BindView(R.id.signupSignInLink)
    lateinit var linkSignUp: TextView

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

        setContentView(R.layout.activity_sign_in)
        ButterKnife.bind(this)



        btnLogin.setOnClickListener {
            if(validate()) presenter.signIn(inputEmail.text.toString(), inputPassword.text.toString())
        }

        inputPassword.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            // If the event is a key-down event on the "enter" button
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Perform action on key press
                if(validate()) presenter.signIn(inputEmail.text.toString(), inputPassword.text.toString())
                return@OnKeyListener true
            }
            false
        })

        linkSignUp.setOnClickListener {
            startActivity<SignUpActivity>()
        }

        presenter.isAuthenticated() //if authenticated, sign in
    }

    fun validate(): Boolean {
        if(inputEmail.text.toString().equals("")) {
            inputEmail.error = "enter an email address"
            return false
        } else if(inputPassword.text.toString().equals("")) {
            inputPassword.error = "enter a password"
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
            title(R.string.auth_failed_title)
            cancellable(true)
        }.show()

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

}