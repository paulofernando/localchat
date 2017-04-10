package site.paulo.localchat.ui.user

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.FunctionSubscriber
import rx.lang.kotlin.addTo
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.model.chatgeo.Chat
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.injection.ConfigPersistent
import site.paulo.localchat.ui.dashboard.nearby.ChatContract
import timber.log.Timber
import javax.inject.Inject


@ConfigPersistent
class ChatPresenter
@Inject
constructor(private val dataManager: DataManager,
            private val firebaseDatabase: FirebaseDatabase) : ChatContract.Presenter() {

    val CHILD_MESSAGES = "messages"
    val CHILD_USERS = "users"

    private lateinit var databaseRef: DatabaseReference

    private val groupsChildEventListener: ChildEventListener? = null

    override fun loadChats() {
        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var chatList: MutableList<Chat> = mutableListOf<Chat>()
                for (chatSnapshot in dataSnapshot.children) {
                    chatList.add(chatSnapshot.getValue(Chat::class.java))
                }
                loadProfilePicture(chatList)
                view.showChats(chatList.toList())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("loadMessages", "loadPost:onCancelled", databaseError.toException())
            }
        }

        firebaseDatabase.getReference("chats").addValueEventListener(valueEventListener)
    }

    override fun loadProfilePicture(chatList: List<Chat>) {
        //TODO
    }

    private val compositeSubscription = CompositeSubscription()

    override fun detachView() {
        super.detachView()
        compositeSubscription.clear()
    }

}