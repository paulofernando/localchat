package site.paulo.localchat.ui.signin

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import butterknife.ButterKnife
import site.paulo.localchat.R
import site.paulo.localchat.ui.base.BaseActivity
import javax.inject.Inject

class SignInActivity : BaseActivity(), SignInContract.View {

    @Inject
    lateinit var presenter: SignInPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        ButterKnife.bind(this)

        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
        }

        setContentView(R.layout.activity_sign_in)

        presenter.attachView(this)
        //presenter.loadRibots()
    }

    override fun showSuccessFullSignIn() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

}