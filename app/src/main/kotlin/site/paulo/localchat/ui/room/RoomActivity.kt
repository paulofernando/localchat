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
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import at.markushi.ui.CircleButton
import butterknife.BindView
import butterknife.ButterKnife
import site.paulo.localchat.R
import site.paulo.localchat.data.MessagesManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.data.model.firebase.SummarizedUser
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.utils.CircleTransform
import site.paulo.localchat.ui.utils.Utils
import site.paulo.localchat.ui.utils.getFirebaseId
import site.paulo.localchat.ui.utils.loadUrlCircle
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

    @BindView(R.id.messageRoomTxt)
    lateinit var messageText: EditText

    @BindView(R.id.sendRoomBtn)
    lateinit var sendBtn: CircleButton

    @BindView(R.id.toolbarRoom)
    lateinit var toolbar: Toolbar

    @BindView(R.id.messagesRoomList)
    lateinit var messagesList: RecyclerView

    @BindView(R.id.otherUserImg)
    lateinit var otherUserPic: ImageView

    @BindView(R.id.attachImageRoomImg)
    lateinit var attachImage: ImageView

    @BindView(R.id.attachImageRoomProgressImg)
    lateinit var attachImageProgress: ProgressBar

    var emptyRoom: Boolean = false

    var chat: Chat? = null
    var otherUser: User? = null
    var chatId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        presenter.attachView(this)
        setContentView(R.layout.activity_room)
        ButterKnife.bind(this)

        this.chat = intent.getParcelableExtra<Chat>("chat") //just passed from chat fragment

        this.chatId = intent.getStringExtra("chatId") //just passed from nearby users fragment
        this.otherUser = intent.getParcelableExtra<User>("otherUser") //just passed from nearby users fragment

        messagesList.adapter = roomAdapter
        messagesList.layoutManager = LinearLayoutManager(this)
        (messagesList.getLayoutManager() as LinearLayoutManager).stackFromEnd = true


        if ((otherUser != null) && //come from nearby users fragment
                !currentUserManager.getUser().chats.containsKey(Utils.getFirebaseId(otherUser!!.email))) {
            emptyRoom = true
        } else {
            if (chat == null) //just have the chat id
                presenter.getChatData(chatId!!)
            else showChat(chat!!)
        }

        sendBtn.setOnClickListener {
            if (!emptyRoom)
                presenter.sendMessage(ChatMessage(currentUserManager.getUserId(),
                        messageText.text.toString()), chat?.id ?: chatId!!)
            else {
                //first message between users, creates a room before send it
                chat = presenter.createNewRoom(this.otherUser!!)
                chatId = chat?.id
                presenter.sendMessage(ChatMessage(currentUserManager.getUserId(),
                        messageText.text.toString()), chat?.id ?: chatId!!)
                presenter.registerMessagesListener(chat?.id ?: chatId!!)
                emptyRoom = false
            }
        }
        MessagesManager.readMessages(chat?.id ?: chatId!!, currentUserManager.getUserId()) //mark all messages as read
        configureToolbar()

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
        messagesList.smoothScrollToPosition(roomAdapter.getItemCount())
        roomAdapter.notifyItemInserted(roomAdapter.itemCount - 1)
    }

    override fun addMessage(message: ChatMessage) {
        roomAdapter.messages.add(message)
        messagesList.smoothScrollToPosition(roomAdapter.getItemCount())
        roomAdapter.notifyItemInserted(roomAdapter.itemCount - 1)
        MessagesManager.readMessages(chat?.id ?: chatId!!, currentUserManager.getUserId())
    }

    override fun showEmptyChatRoom() {
        emptyRoom = true
    }

    override fun showLoadingImage() {
        attachImage.visibility = View.INVISIBLE
        attachImageProgress.visibility = View.VISIBLE
    }

    override fun hideLoadingImage() {
        attachImage.visibility = View.VISIBLE
        attachImageProgress.visibility = View.INVISIBLE
    }

    override fun cleanMessageField() {
        messageText.text.clear()
    }

    override fun messageSent(message: ChatMessage) {
        Timber.i("Message sent: " + message.message)
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_PHOTO_PICKER && resultCode == Activity.RESULT_OK) {
            presenter.uploadImage(data.data, this.chatId!!)
        }
    }

    fun showImagePicker(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER)
    }

    private fun configureToolbar() {
        if (this.otherUser != null) {
            toolbar.title = otherUser?.name ?: ""
            otherUserPic.loadUrlCircle(otherUser?.pic) {
                request -> request.transform(CircleTransform())
            }
        } else if (this.chat != null) {
            var otherUserIndex: Int = 0
            if ((chat as Chat).users.keys.indexOf(currentUserManager.getUserId()) == 0) otherUserIndex = 1
            var summarizedUser: SummarizedUser? = (this.chat as Chat).users.get((chat as Chat).users.keys.elementAt(otherUserIndex))
            chatId = (chat as Chat)?.id

            toolbar.title = summarizedUser?.name
            otherUserPic.loadUrlCircle(summarizedUser?.pic) {
                request ->
                request.transform(CircleTransform())
            }
        }
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }


}