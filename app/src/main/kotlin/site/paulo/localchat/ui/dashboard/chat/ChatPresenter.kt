package site.paulo.localchat.ui.user

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import rx.subscriptions.CompositeSubscription
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.model.chatgeo.Chat
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.injection.ConfigPersistent
import site.paulo.localchat.ui.dashboard.nearby.ChatContract
import javax.inject.Inject


@ConfigPersistent
class ChatPresenter
@Inject
constructor(private val dataManager: DataManager,
            private val firebaseDatabase: FirebaseDatabase) : ChatContract.Presenter() {

    val CHILD_CHATS = "chats"
    val CHILD_USERS = "users"

    override fun loadChatRooms() {
        firebaseDatabase.getReference(CHILD_USERS).child("kGbfdjuhsug")
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

        /*val valueEventListener = object : ValueEventListener {
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

        firebaseDatabase.getReference("chats").addValueEventListener(valueEventListener)*/
    }

    override fun loadChat(chatId: String) {
        firebaseDatabase.getReference(CHILD_CHATS).child(chatId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    view.showChat(dataSnapshot.getValue(Chat::class.java))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println(databaseError.toString())
                }
            })
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