package site.paulo.localchat.ui.dashboard.nearby

import site.paulo.localchat.data.model.chatgeo.Chat
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object ChatContract {

    interface View : MvpView {
        fun showChats(chats: List<Chat>)
        fun showChat(chat: Chat)
        fun showChatsEmpty()
        fun showError()
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun loadChatRooms()
        abstract fun loadChat(chatId: String)
        abstract fun loadProfilePicture(chatList: List<Chat>)
    }
}