package site.paulo.localchat.ui.user

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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
import site.paulo.localchat.ui.utils.Utils
import site.paulo.localchat.ui.utils.getCurrentUserId
import timber.log.Timber
import javax.inject.Inject


@ConfigPersistent
class ChatPresenter
@Inject
constructor(private val dataManager: DataManager,
            private val firebaseDatabase: FirebaseDatabase) : ChatContract.Presenter() {

    val CHILD_CHATS = "chats"
    val CHILD_USERS = "users"

    override fun loadChatRooms() {
        firebaseDatabase.getReference(CHILD_USERS).child(Utils.getCurrentUserId())
            .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println("We're done loading user information")
                val user = dataSnapshot.getValue(User::class.java)
                loadChat(user.chats.keys.elementAt(0)) //TODO
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println(databaseError.toString())
            }
        })
    }

    override fun loadChat(chatId: String) {
        dataManager.getChatRooms(chatId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(FunctionSubscriber<Chat>()
                .onNext {
                    view.showChat(it)
                }
                .onError {
                    Timber.e(it, "There was an error loading a chat room.")
                    view.showError()
                }
            ).addTo(compositeSubscription)
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