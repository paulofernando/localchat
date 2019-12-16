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
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import com.anupcowkur.reservoir.Reservoir
import java.io.IOException


class MessagesManager {

    companion object Factory {
        /** Every message is stored here */ //TODO just stored the last x messages by chat
        val chatMessages = mutableMapOf<String, MutableList<ChatMessage>>()
        val chatListeners = mutableMapOf<String, MessagesListener>()

        fun add(chatMessage: ChatMessage, chatId: String) {
            chatMessages[chatId]?.add(chatMessage)
            chatListeners[chatId]?.messageReceived(chatMessage)
        }

        fun unreadMessages(chatId: String, userId: String) {
            if(!chatMessages.containsKey(chatId)) {
                chatMessages.put(chatId, mutableListOf<ChatMessage>())
            }
            try {
                val key = "$chatId-unread-$userId"
                if(!Reservoir.contains(key)) {
                    Reservoir.put(key, AtomicInteger(0))
                }
                val unread = Reservoir.get<AtomicInteger>(key, AtomicInteger::class.java)
                Reservoir.put(key, unread.incrementAndGet())
            } catch (e: IOException) {
                Timber.e(e.message)
            }
        }

        fun readMessages(chatId: String, userId: String) {
            try {
                Reservoir.put("$chatId-unread-$userId", AtomicInteger(0))
            } catch (e: IOException) {
                Timber.e(e.message)
            }
        }

        fun getUnreadMessages(chatId: String, userId: String): Int {
            try {
                val key = "$chatId-unread-$userId"
                if(!Reservoir.contains(key)) {
                    Reservoir.put(key, AtomicInteger(0))
                }
                return Reservoir.get<AtomicInteger>(key, AtomicInteger::class.java).get()
            } catch (e: Exception) {
                Timber.e(e)
            }
            return 0
        }

        fun hasUnread(chatId: String, userId: String): Boolean {
            return getUnreadMessages(chatId, userId) > 0
        }

        /**
         * Register a message listener and returns the messages already received
         */
        fun registerListener(messageListener: MessagesListener, chatId: String): MutableList<ChatMessage>? {
            chatListeners.put(chatId, messageListener)
            return chatMessages[chatId]
        }
    }

}