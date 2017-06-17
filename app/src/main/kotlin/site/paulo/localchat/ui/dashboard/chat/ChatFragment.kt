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

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import site.paulo.localchat.R
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.ui.base.BaseFragment
import site.paulo.localchat.ui.user.ChatAdapter
import site.paulo.localchat.ui.user.ChatPresenter
import site.paulo.localchat.ui.utils.Utils
import site.paulo.localchat.ui.utils.getFirebaseId
import javax.inject.Inject

class ChatFragment : BaseFragment(), ChatContract.View {

    @Inject
    lateinit var presenter: ChatPresenter

    @Inject
    lateinit var chatsAdapter: ChatAdapter

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @BindView(R.id.chatRoomsList)
    lateinit var chatsList: RecyclerView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        activityComponent.inject(this)
        val rootView = inflater!!.inflate(R.layout.fragment_dashboard_chats, container, false)
        ButterKnife.bind(this, rootView)

        chatsList.adapter = chatsAdapter
        chatsList.layoutManager = LinearLayoutManager(activity)

        presenter.attachView(this)
        presenter.loadChatRooms(Utils.getFirebaseId(firebaseAuth.getCurrentUser()?.email!!))

        return rootView
    }

    override fun showChats(chats: List<Chat>) {
        chatsAdapter.chats = chats.toMutableList()
        chatsAdapter.notifyDataSetChanged()
    }

    override fun showChat(chat: Chat) {
        chatsAdapter.chats.add(chat)
        chatsAdapter.notifyDataSetChanged()
    }

    override fun showChatsEmpty() {
        chatsAdapter.chats = mutableListOf<Chat>()
        chatsAdapter.notifyDataSetChanged()
        Toast.makeText(activity, R.string.empty_chat, Toast.LENGTH_LONG).show()
    }

    override fun showError() {
        Toast.makeText(activity, R.string.error_loading_chat, Toast.LENGTH_LONG).show()
    }

}