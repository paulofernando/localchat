package site.paulo.localchat.ui.settings.profile

import site.paulo.localchat.ui.base.MvpView

object ProfileContract {

    interface View : MvpView {
        fun showCurrentUserData()
        fun editName(view: android.view.View)
        fun cancelNameEdition(view : android.view.View)
        fun confirmNameEdition(view : android.view.View)
    }

}