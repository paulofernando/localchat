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
import android.widget.EditText
import android.widget.Toast
import at.markushi.ui.CircleButton
import butterknife.BindView
import butterknife.ButterKnife
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import site.paulo.localchat.R
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.ui.base.BaseActivity
import timber.log.Timber
import javax.inject.Inject

class RoomActivity : BaseActivity() , RoomContract.View {

    internal val RC_PHOTO_PICKER = 1

    @Inject
    lateinit var currentUserManager: CurrentUserManager

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

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

    var emptyRoom: Boolean = false

    var chat: Chat? = null
    var otherUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        setContentView(R.layout.activity_room)
        ButterKnife.bind(this)
        presenter.attachView(this)

        this.chat = intent.getParcelableExtra<Chat>("chat") //just passed from chat fragment
        val chatId: String? = intent.getStringExtra("chatId") //just passed from nearby users fragment
        this.otherUser = intent.getParcelableExtra<User>("otherUser") //just passed from nearby users fragment

        if(chat == null) //just have the chat id
            presenter.getChatData(chatId!!)
        else showChat(chat!!)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        messagesList.adapter = roomAdapter
        messagesList.layoutManager = LinearLayoutManager(this)
        (messagesList.getLayoutManager() as LinearLayoutManager).stackFromEnd = true

        sendBtn.setOnClickListener {
            //TODO create new room just after send first message
            if(!emptyRoom) presenter.sendMessage(ChatMessage(currentUserManager.getUserId(), messageText.text.toString()), chat!!.id)
            else {
                chat = presenter.createNewRoom(this.otherUser!!)
                presenter.sendMessage(ChatMessage(currentUserManager.getUserId(), messageText.text.toString()), chat!!.id)
                presenter.registerRoomListener(chat!!.id)
                emptyRoom = false

                Timber.i("Chat created: " + chat!!.id)
            }
        }

    }

    override fun showChat(chat: Chat) {
        this.chat = chat

        if(otherUser == null) {
            var otherUserIndex: Int = 0
            if (chat.users.keys.indexOf(currentUserManager.getUserId()) == 0) otherUserIndex = 1
            toolbar.title = chat.users.get(chat.users.keys.elementAt(otherUserIndex))?.name ?: ""
        } else {
            toolbar.title = otherUser?.name ?: ""
        }

        presenter.registerRoomListener(chat.id)
    }

    override fun showError() {
        Toast.makeText(this, R.string.error_loading_chat_room_data, Toast.LENGTH_LONG).show()
    }

    override fun addMessage(message: ChatMessage) {
        roomAdapter.messages.add(message)
        messagesList.smoothScrollToPosition(roomAdapter.getItemCount())
        roomAdapter.notifyItemInserted(roomAdapter.itemCount - 1)
    }

    override fun showEmptyChatRoom() {
        emptyRoom = true
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
        when(item.itemId) {
            R.id.action_attach_image -> {
                showImagePicker()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun showImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_PHOTO_PICKER && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data.data

            // Get a reference to the location where we'll store our photos
            var storageRef: StorageReference = firebaseStorage.getReference("chat_pics")
            // Get a reference to store file at chat_photos/<FILENAME>
            val photoRef = storageRef.child(selectedImageUri.lastPathSegment)

            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri).addOnSuccessListener { taskSnapshot ->
                Timber.i("Image sent successfully!")
                val downloadUrl = taskSnapshot?.downloadUrl
                presenter.sendMessage(ChatMessage(currentUserManager.getUserId(), downloadUrl!!.toString()), chat!!.id)
            }
        }
    }


}