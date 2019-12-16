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

package site.paulo.localchat.ui.utils

import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.SummarizedUser

class Utils {
    companion object {}
}

fun Utils.Companion.getFirebaseId(email: String): String {
    if(!email.equals("")) return email.replace(".", "_", false)
    else return ""
}

/**
 * Get the other user of a specific chat
 */
fun Utils.Companion.getChatFriend(loggedUserId: String, chat: Chat): SummarizedUser? {
    var userIndex = 0
    if (chat.users.keys.indexOf(loggedUserId) == 0)
        userIndex = 1

    return chat.users.get(chat.users.keys.elementAt(userIndex))
}
