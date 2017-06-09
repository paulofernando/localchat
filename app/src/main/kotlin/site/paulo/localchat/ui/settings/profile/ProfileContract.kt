package site.paulo.localchat.ui.settings.profile

import site.paulo.localchat.ui.base.MvpView

object ProfileContract {

    interface View : MvpView {
        fun showCurrentUserData()
    }

}