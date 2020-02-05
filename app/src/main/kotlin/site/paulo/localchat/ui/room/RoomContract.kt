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
import site.paulo.localchat.data.MessagesListener
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.data.model.firebase.NearbyUser
import site.paulo.localchat.data.model.firebase.SummarizedUser
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object RoomContract {

    interface View : MvpView {
        fun addMessage(message: ChatMessage)
        fun loadOldMessages(messages: MutableList<ChatMessage>?)
        fun messageSent(message: ChatMessage)
        fun showChat(chatId: String)
        fun showEmptyChatRoom()
        fun showError()
        fun showLoadingImage()
        fun hideLoadingImage()
        fun cleanMessageField()
        fun cleanNotifications()
    }

    abstract class Presenter : BaseMvpPresenter<View>(), MessagesListener {
        abstract fun sendMessage(message: ChatMessage, chatId: String)
        abstract fun getChatData(chatId: String)
        abstract fun registerMessagesListener(chatId: String)
        abstract fun unregisterMessagesListener(chatId: String)
        abstract fun createNewRoom(otherUser: SummarizedUser, otherEmail: String): Chat
        abstract fun uploadImage(selectedImageUri: Uri, roomId: String)
    }
}
