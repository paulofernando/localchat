package site.paulo.localchat.ui.dashboard

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class DashboardPresenter
@Inject
constructor(private val firebaseAuth: FirebaseAuth): DashboardContract.Presenter() {

    override fun logout() {
        firebaseAuth.signOut()
    }


}