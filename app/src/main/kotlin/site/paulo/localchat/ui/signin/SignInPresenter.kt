package site.paulo.localchat.ui.signin

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.kelvinapps.rxfirebase.RxFirebaseAuth
import timber.log.Timber
import javax.inject.Inject


class SignInPresenter
@Inject
constructor(private val firebaseAuth: FirebaseAuth) : SignInContract.Presenter() {

    override fun signIn(email: String, password: String) {
        RxFirebaseAuth.signInWithEmailAndPassword(firebaseAuth, email, password)
            .subscribe({
                Timber.i("signIn", "signInWithEmail:onComplete")
                view.showFailSignIn()
            }, {
                Timber.e("signIn", "signInWithEmail:failed")
                view.showSuccessFullSignIn()
            })
    }

    override fun isAuthenticated() {
        firebaseAuth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                Log.d("isAuthenticated", "onAuthStateChanged:signed_in:" + firebaseAuth.currentUser?.uid)
                view.showSuccessFullSignIn()
            } else {
                Log.d("isAuthenticated", "onAuthStateChanged:signed_out")
            }
        }
    }


}