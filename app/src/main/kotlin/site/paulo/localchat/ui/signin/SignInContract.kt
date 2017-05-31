package site.paulo.localchat.ui.signin

import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object SignInContract {

    interface View : MvpView {
        fun showSuccessFullSignIn()
        fun showFailSignIn()
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun signIn(email: String, password: String)
        abstract fun isAuthenticated()
    }
}
