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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.anupcowkur.reservoir.Reservoir
import kotlinx.android.synthetic.main.activity_room.*
import site.paulo.localchat.R
import site.paulo.localchat.data.MessagesManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.*
import site.paulo.localchat.exception.MissingCurrentUserException
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.utils.*
import timber.log.Timber
import javax.inject.Inject

class RoomActivity : BaseActivity(), RoomContract.View {

    internal val RC_PHOTO_PICKER = 1

    @Inject
    lateinit var currentUserManager: CurrentUserManager

    @Inject
    lateinit var presenter: RoomPresenter

    @Inject
    lateinit var roomAdapter: RoomAdapter

    private var emptyRoom: Boolean = false
    var chat: Chat? = null
    var chatFriend: NearbyUser? = null
    var chatId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivity()

        this.chatId = intent.getStringExtra("chatId")
        this.chatFriend = intent.getParcelableExtra<NearbyUser>("otherUser") //just passed from nearby users fragment
        if (Reservoir.contains(chatId)) {
            this.chat = Reservoir.get<Chat>(chatId, Chat::class.java)
        }

        messagesRoomList.adapter = roomAdapter
        messagesRoomList.layoutManager = LinearLayoutManager(this)
        //(messagesRoomList.layoutManager as LinearLayoutManager).stackFromEnd = true //uncomment to add messages from bottom to top

        if ((chatFriend != null) && //come from nearby users fragment
                !currentUserManager.getUser()!!.chats.containsKey(Utils.getFirebaseId(chatFriend!!.email))) {
            emptyRoom = true
        } else {
            if (chat == null) //only have the chat id
                presenter.getChatData(chatId!!)
            else showChat(chat!!)
        }

        sendRoomBtn.setOnClickListener {
            if (!emptyRoom)
                presenter.sendMessage(
                        ChatMessage(currentUserManager.getUserId(), messageRoomTxt.text.toString()),
                        chat?.id ?: chatId!!)
            else {
                //first message between users, creates a room before send it
                chat = presenter.createNewRoom(this.chatFriend!!)
                chatId = chat?.id
                presenter.sendMessage(
                        ChatMessage(currentUserManager.getUserId(), messageRoomTxt.text.toString()),
                        chat?.id ?: chatId!!)
                presenter.registerMessagesListener(chat?.id ?: chatId!!)
                emptyRoom = false
            }
        }

        MessagesManager.readMessages(chat?.id ?: chatId!!, currentUserManager.getUserId()) //mark all messages as read
        cleanNotifications()
        configureToolbar()
    }

    private fun setupActivity() {
        activityComponent.inject(this)
        presenter.attachView(this)
        setContentView(R.layout.activity_room)
    }

    override fun showChat(chat: Chat) {
        presenter.registerMessagesListener(chat.id)
    }

    override fun showError() {
        Toast.makeText(this, R.string.error_loading_chat_room_data, Toast.LENGTH_LONG).show()
    }

    override fun loadOldMessages(messages: MutableList<ChatMessage>?) {
        if(messages!= null) {
            for(message in messages) {
                roomAdapter.messages.add(message)
            }
        }
        messagesRoomList.smoothScrollToPosition(roomAdapter.getItemCount())
        roomAdapter.notifyItemInserted(roomAdapter.itemCount - 1)
    }

    override fun addMessage(message: ChatMessage) {
        roomAdapter.messages.add(message)
        messagesRoomList.smoothScrollToPosition(roomAdapter.getItemCount())
        roomAdapter.notifyItemInserted(roomAdapter.itemCount - 1)
    }

    override fun showEmptyChatRoom() {
        emptyRoom = true
    }

    override fun showLoadingImage() {
        attachImageRoomImg.visibility = View.INVISIBLE
        attachImageRoomProgressImg.visibility = View.VISIBLE
    }

    override fun hideLoadingImage() {
        attachImageRoomImg.visibility = View.VISIBLE
        attachImageRoomProgressImg.visibility = View.INVISIBLE
    }

    override fun cleanMessageField() {
        messageRoomTxt.text.clear()
    }

    override fun cleanNotifications() {
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.cancel(chatId.hashCode())
    }

    override fun messageSent(message: ChatMessage) {
        Timber.i("Message sent: %s", message.message)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_room, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregisterMessagesListener(chat?.id ?: chatId!!)
        presenter.detachView()
        MessagesManager.readMessages(chat?.id ?: chatId!!, currentUserManager.getUserId()) //mark all messages as read
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_PHOTO_PICKER && resultCode == Activity.RESULT_OK) {
            presenter.uploadImage(data?.data!!, this.chatId!!)
        }
    }

    fun showImagePicker(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER)
    }

    private fun configureToolbar() {
        if (chatFriend != null) {
            toolbarRoom.title = chatFriend?.name ?: ""
            otherUserImg.loadUrlCircle(chatFriend?.pic) {
                request -> request.transform(CircleTransform())
            }
        } else if (chat != null) { //configure toolbar when user come from chat list //TODO improve it
            val summarizedUser = Utils.getChatFriend(currentUserManager.getUserId(), chat as Chat)
            chatId = (chat as Chat).id

            toolbarRoom.title = summarizedUser?.name
            otherUserImg.loadUrlCircle(summarizedUser?.pic) {
                request ->
                request.transform(CircleTransform())
            }
        }
        setSupportActionBar(toolbarRoom)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }


}