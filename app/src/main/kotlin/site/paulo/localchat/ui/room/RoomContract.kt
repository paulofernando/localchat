package site.paulo.localchat.ui.room

import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object RoomContract {

    interface View : MvpView {
        fun addMessage(message: ChatMessage)
        fun messageSent(message: ChatMessage)
        fun cleanMessageField()
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun sendMessage(message: ChatMessage, chatId: String)
        abstract fun registerRoomListener(chatId: String)
    }
}
