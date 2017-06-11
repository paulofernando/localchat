package site.paulo.localchat.ui.room

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import at.markushi.ui.CircleButton
import butterknife.BindView
import butterknife.ButterKnife
import site.paulo.localchat.R
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.chatgeo.Chat
import site.paulo.localchat.data.model.chatgeo.ChatMessage
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.utils.Utils
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

        var otherUserIndex: Int = 0;
        var chat: Chat = intent.getParcelableExtra<Chat>("chat");
        if (chat.users.keys.indexOf(currentUserManager.getUserId()) == 0) otherUserIndex = 1 //TODO change to getCurrentUserId
        toolbar.title = chat.users.get(chat.users.keys.elementAt(otherUserIndex))!!.name

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        messagesList.adapter = roomAdapter
        messagesList.layoutManager = LinearLayoutManager(this)

        presenter.attachView(this)
        presenter.registerRoomListener(chat.id)

        sendBtn.setOnClickListener {
            presenter.sendMessage(ChatMessage(currentUserManager.getUserId(), messageText.text.toString()), "cH58hajvh")
        }
    }

    override fun addMessage(message: ChatMessage) {
        roomAdapter.messages.add(message)
        messagesList.smoothScrollToPosition(roomAdapter.getItemCount())
        roomAdapter.notifyItemInserted(roomAdapter.itemCount - 1)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_room, menu)
        return true
    }

    override fun cleanMessageField() {
        messageText.text.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

}