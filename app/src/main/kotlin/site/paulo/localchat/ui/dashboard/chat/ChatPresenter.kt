package site.paulo.localchat.ui.user

import com.google.firebase.auth.FirebaseAuth
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
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.chatgeo.Chat
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.injection.ConfigPersistent
import site.paulo.localchat.ui.dashboard.nearby.ChatContract
import site.paulo.localchat.ui.utils.Utils
import site.paulo.localchat.ui.utils.getFirebaseId
import timber.log.Timber
import javax.inject.Inject


@ConfigPersistent
class ChatPresenter
@Inject
constructor(private val dataManager: DataManager,
            private val firebaseDatabase: FirebaseDatabase,
            private val firebaseAuth: FirebaseAuth,
            private val currentUserManager: CurrentUserManager) : ChatContract.Presenter() {

    val CHILD_USERS = "users"

    override fun loadChatRooms() {

        /*dataManager.getUser(Utils.getCurrentUserId())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(FunctionSubscriber<User>()
                .onNext {
                    loadChatRoom(it.)
                }
                .onError {
                    Timber.e(it, "There was an error loading a chat room.")
                    view.showError()
                }
            ).addTo(compositeSubscription)*/

        val userEmail = firebaseAuth.currentUser?.email
        firebaseDatabase.getReference(CHILD_USERS).child(Utils.getFirebaseId(userEmail!!))
            .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Timber.i("We're done loading user information")
                val user = dataSnapshot.getValue(User::class.java)
                loadChatRoom(user.chats.keys.elementAt(0))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.e(databaseError.toString())
            }
        })
    }

    override fun loadChatRoom(chatId: String) {
        dataManager.getChatRoom(chatId)
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