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
import site.paulo.localchat.R
import site.paulo.localchat.data.model.chatgeo.Chat
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.ui.base.BaseFragment
import site.paulo.localchat.ui.user.ChatAdapter
import site.paulo.localchat.ui.user.ChatPresenter
import javax.inject.Inject

class ChatFragment : BaseFragment(), ChatContract.View {

    @Inject
    lateinit var presenter: ChatPresenter

    @Inject
    lateinit var chatsAdapter: ChatAdapter

    @BindView(R.id.usersNearbyList)
    lateinit var chatsList: RecyclerView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        activityComponent.inject(this)
        val rootView = inflater!!.inflate(R.layout.fragment_dashboard, container, false)
        ButterKnife.bind(this, rootView)

        chatsList.adapter = chatsAdapter
        chatsList.layoutManager = LinearLayoutManager(activity)

        presenter.attachView(this)
        presenter.loadChats()

        return rootView
    }

    override fun showChats(chats: List<Chat>) {
        chatsAdapter.chats = chats
        chatsAdapter.notifyDataSetChanged()
    }

    override fun showChatsEmpty() {
        chatsAdapter.chats = emptyList()
        chatsAdapter.notifyDataSetChanged()
        Toast.makeText(activity, R.string.empty_ribots, Toast.LENGTH_LONG).show()
    }

    override fun showError() {
        Toast.makeText(activity, R.string.error_loading_ribots, Toast.LENGTH_LONG).show()
    }

}