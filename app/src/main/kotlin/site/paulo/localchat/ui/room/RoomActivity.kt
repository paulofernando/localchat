package site.paulo.localchat.ui.room

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.widget.EditText
import android.widget.Toast
import at.markushi.ui.CircleButton
import butterknife.BindView
import butterknife.ButterKnife
import site.paulo.localchat.R
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.ui.base.BaseActivity
import timber.log.Timber
import javax.inject.Inject

class RoomActivity : BaseActivity() , RoomContract.View {

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

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }


}