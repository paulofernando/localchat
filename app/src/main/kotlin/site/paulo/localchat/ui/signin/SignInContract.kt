package site.paulo.localchat.ui.signin

import site.paulo.localchat.data.model.ribot.Ribot
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object SignInContract {

    interface View : MvpView {
        fun showSuccessFullSignIn()
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun signIn(email: String)
    }
}
