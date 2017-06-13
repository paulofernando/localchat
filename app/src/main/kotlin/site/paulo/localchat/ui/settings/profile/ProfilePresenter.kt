package site.paulo.localchat.ui.settings

import com.google.firebase.database.DatabaseReference
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.remote.FirebaseHelper
import site.paulo.localchat.ui.settings.profile.ProfileContract
import timber.log.Timber
import javax.inject.Inject

class ProfilePresenter
@Inject
constructor(private val dataManager: DataManager, private val currentUserManager: CurrentUserManager) : ProfileContract.Presenter() {

    override fun updateUserData(dataType: FirebaseHelper.Companion.UserDataType, value:String) {
        val completionListener = DatabaseReference.CompletionListener { databaseError, databaseReference ->
            Timber.i("User data updated")
            when(dataType) {
                FirebaseHelper.Companion.UserDataType.NAME -> currentUserManager.setUserName(value)
                FirebaseHelper.Companion.UserDataType.AGE -> currentUserManager.setAge(value.toLong())
            }
        }
        dataManager.updateUserData(dataType, value, completionListener)

    }

}