package site.paulo.localchat.ui.settings

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.chatgeo.ChatMessage
import site.paulo.localchat.data.model.chatgeo.User
import timber.log.Timber
import javax.inject.Inject

class SettingsPresenter
@Inject
constructor(private val firebaseDatabase: FirebaseDatabase, private val currentUserManager: CurrentUserManager) : SettingsContract.Presenter() {

    override fun loadCurrentUser() {
        view.showCurrentUserData(currentUserManager.getUser())
    }

    override fun registerProfileListener() {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {
                Timber.i("registerProfileListener:", dataSnapshot.getValue(User::class.java))
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        firebaseDatabase.getReference("users")
            .child(currentUserManager.getUserId())
            .addChildEventListener(childEventListener)
    }

}