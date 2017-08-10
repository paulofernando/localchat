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

package site.paulo.localchat.data

import site.paulo.localchat.data.model.firebase.ChatMessage

class MessagesManager {

    companion object Factory {
        /** Every message is stored here */ //TODO just stored the last x messages by chat
        val chatMessages = mutableMapOf<String, MutableList<ChatMessage>>()
        val chatListeners = mutableMapOf<String, MessagesListener>()

        fun add(chatMessage: ChatMessage, chatId: String) {
            if(!chatMessages.containsKey(chatId)) {
                chatMessages.put(chatId, mutableListOf<ChatMessage>())
            }
            chatMessages.get(chatId)?.add(chatMessage)
            chatListeners.get(chatId)?.messageReceived(chatMessage)
        }

        fun registerListener(messageListener: MessagesListener, chatId: String) {
            chatListeners.put(chatId, messageListener)
        }
    }

}