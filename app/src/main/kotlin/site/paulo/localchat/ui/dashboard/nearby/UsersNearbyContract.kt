package site.paulo.localchat.ui.dashboard.nearby

import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object UsersNearbyContract {

    interface View: MvpView {
        fun showNearbyUsers(users: List<User>)
        fun showNearbyUsersEmpty()
        fun showError()
    }

    abstract class Presenter: BaseMvpPresenter<View>() {
        abstract fun loadNearbyUsers()
    }
}
