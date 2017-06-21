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

package site.paulo.localchat.ui.room

import android.net.Uri
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object RoomContract {

    interface View : MvpView {
        fun addMessage(message: ChatMessage)
        fun messageSent(message: ChatMessage)
        fun showChat(chat: Chat)
        fun showEmptyChatRoom()
        fun showError()
        fun showLoadingImage()
        fun hideLoadingImage()
        fun cleanMessageField()
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun sendMessage(message: ChatMessage, chatId: String)
        abstract fun getChatData(chatId: String)
        abstract fun registerRoomListener(chatId: String)
        abstract fun createNewRoom(otherUser: User): Chat
        abstract fun uploadImage(selectedImageUri: Uri, chatId: String)
    }
}
