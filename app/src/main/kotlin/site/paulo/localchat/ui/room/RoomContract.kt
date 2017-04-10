package site.paulo.localchat.ui.signin

import site.paulo.localchat.data.model.chatgeo.ChatMessage
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object RoomContract {

    interface View : MvpView {
        fun addMessage(message: ChatMessage)
        fun cleanMessageField()
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun loadMessages()
        abstract fun sendMessage(message: ChatMessage, chatId: String)
        abstract fun registerRoomListener()
    }
}
