/*
 * Copyright 2017 Paulo Fernando
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package site.paulo.localchat.ui.dashboard.nearby

import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object ChatContract {

    interface View : MvpView {
        fun showChats(chats: List<Chat>)
        fun showChat(chat: Chat)
        fun showChatsEmpty()
        fun showError()
        fun messageReceived(chatMessage: ChatMessage)
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun loadChatRooms(userId: String)
        abstract fun loadChatRoom(chatId: String)
        abstract fun loadProfilePicture(chatList: List<Chat>)
    }
}
