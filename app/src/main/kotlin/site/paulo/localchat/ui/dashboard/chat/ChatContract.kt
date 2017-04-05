package site.paulo.localchat.ui.dashboard.nearby

import site.paulo.localchat.data.model.chatgeo.Chat
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object ChatContract {

    interface View : MvpView {
        fun showChats(users: List<Chat>)
        fun showChatsEmpty()
        fun showError()
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun loadChats()
    }
}
