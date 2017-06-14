package site.paulo.localchat.ui.settings

import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object SettingsContract {

    interface View : MvpView {
        fun showCurrentUserData(user: User)
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun loadCurrentUser()
        abstract fun registerProfileListener()
    }
}