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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        setContentView(R.layout.activity_room)
        ButterKnife.bind(this)

        presenter.attachView(this)

        val chatId: String? = intent.getStringExtra("chatId")
        val chat: Chat? = intent.getParcelableExtra<Chat>("chat")

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
            presenter.sendMessage(ChatMessage(currentUserManager.getUserId(), messageText.text.toString()), "cH58hajvh")
        }
    }

    override fun showChat(chat: Chat) {
        var otherUserIndex: Int = 0
        if (chat.users.keys.indexOf(currentUserManager.getUserId()) == 0) otherUserIndex = 1 //TODO change to getCurrentUserId

        toolbar.title = chat.users.get(chat.users.keys.elementAt(otherUserIndex))?.name ?: ""

        presenter.registerRoomListener(chat.id)
    }

    override fun addMessage(message: ChatMessage) {
        roomAdapter.messages.add(message)
        messagesList.smoothScrollToPosition(roomAdapter.getItemCount())
        roomAdapter.notifyItemInserted(roomAdapter.itemCount - 1)
    }

    override fun showEmptyChatRoom() {
        //Toast.makeText(this, R.string.error_loading_chat_room_data, Toast.LENGTH_LONG).show()
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