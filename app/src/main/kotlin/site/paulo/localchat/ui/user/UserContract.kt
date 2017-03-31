package site.paulo.localchat.ui.user

import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object UserContract {

    interface View: MvpView {
        fun showUsers(users: List<User>)
        fun showUsersEmpty()
        fun showError()
    }

    abstract class Presenter: BaseMvpPresenter<View>() {
        abstract fun loadUsers()
    }
}
