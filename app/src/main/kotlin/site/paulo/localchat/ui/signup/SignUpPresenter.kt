package site.paulo.localchat.ui.signup

import com.google.firebase.auth.FirebaseAuth
import com.kelvinapps.rxfirebase.RxFirebaseAuth
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.model.chatgeo.User
import timber.log.Timber
import javax.inject.Inject

class SignUpPresenter
@Inject
constructor(private val dataManager: DataManager, private val firebaseAuth: FirebaseAuth)
    : SignUpContract.Presenter() {

    override fun signUp(email: String, password: String, name: String, age: Long, gender: String) {
        RxFirebaseAuth.createUserWithEmailAndPassword(firebaseAuth, email, password)
            .subscribe({
                Timber.i("Signed up")
                this.registerUser(User(name,age,email,gender))
                view.showSuccessFullSignUp()
            }, {
                Timber.e("Sign up failed")
                view.showFailSignUp()
            })
    }

    private fun registerUser(user: User) {
        dataManager.registerUser(user)
    }
}