package site.paulo.localchat.ui.user

import com.google.firebase.FirebaseApp
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
        dataManager.getUsers()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(FunctionSubscriber<List<User>>()
                .onNext {
                    //println(it.toString());
                    if (it.isEmpty()) view.showChatsEmpty() else view.showChats(it)
                }
                .onError {
                    Timber.e(it, "There was an error loading the users.")
                    view.showError()
                }
            ).addTo(compositeSubscription)
    }

    override fun loadMessages() {
        firebaseDatabase.getReference("chats").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println(dataSnapshot.toString())
                println("We're done loading groups")
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    private val compositeSubscription = CompositeSubscription()

    override fun detachView() {
        super.detachView()
        compositeSubscription.clear()
    }

}