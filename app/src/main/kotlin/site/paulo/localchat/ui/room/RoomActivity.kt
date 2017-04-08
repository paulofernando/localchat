package site.paulo.localchat.ui.room

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import site.paulo.localchat.R
import site.paulo.localchat.data.model.chatgeo.Chat
import site.paulo.localchat.data.model.chatgeo.ChatMessage
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.signin.RoomContract
import site.paulo.localchat.ui.signin.RoomPresenter
import javax.inject.Inject

class RoomActivity : BaseActivity() , RoomContract.View {

    @Inject
    lateinit var presenter: RoomPresenter

    @Inject
    lateinit var roomAdapter: RoomAdapter

    @BindView(R.id.messageTxt)
    lateinit var messageText: EditText

    @BindView(R.id.sendBtn)
    lateinit var sendBtn: Button

    @BindView(R.id.toolbarRoom)
    lateinit var toolbar: Toolbar

    @BindView(R.id.messagesList)
    lateinit var messagesList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        setContentView(R.layout.activity_room)
        ButterKnife.bind(this)

        toolbar.title = intent.getParcelableExtra<Chat>("chat").name
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        messagesList.adapter = roomAdapter
        messagesList.layoutManager = LinearLayoutManager(this)

        presenter.attachView(this)
        presenter.registerRoomListener()

        sendBtn.setOnClickListener {
            presenter.sendMessage(ChatMessage("Username", messageText.text.toString()))
        }
    }

    override fun addMessage(message: ChatMessage) {
        roomAdapter.messages.add(message)
    }

    override fun cleanMessageField() {
        messageText.text.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

}