package site.paulo.localchat.ui.signup

import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object SignUpContract {

    interface View : MvpView {
        fun showSuccessFullSignUp()
        fun validate(): Boolean
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun signUp(email: String, password: String, name: String, age: Long, gender: String)
    }
}
