package site.paulo.localchat.ui.signin

import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object RoomContract {

    interface View : MvpView {
        fun showMessages()
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun loadMessages()
    }
}
