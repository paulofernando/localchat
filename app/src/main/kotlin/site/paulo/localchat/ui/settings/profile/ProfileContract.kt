package site.paulo.localchat.ui.settings.profile

import com.google.firebase.database.DatabaseReference
import site.paulo.localchat.data.remote.FirebaseHelper
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object ProfileContract {

    interface View : MvpView {
        fun showCurrentUserData()

        fun editName(view: android.view.View)
        fun cancelNameEdition(view : android.view.View)
        fun confirmNameEdition(view : android.view.View)

        fun editAge(view: android.view.View)
        fun cancelAgeEdition(view : android.view.View)
        fun confirmAgeEdition(view : android.view.View)
    }

    abstract class Presenter : BaseMvpPresenter<ProfileContract.View>() {
        abstract fun updateUserData(dataType: FirebaseHelper.Companion.UserDataType, value:String)
    }

}