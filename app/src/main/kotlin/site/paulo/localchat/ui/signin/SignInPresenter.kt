package site.paulo.localchat.ui.signin

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class SignInPresenter
@Inject
constructor(private val firebaseAuth: FirebaseAuth) : SignInContract.Presenter() {

    override fun signIn(email: String) {
        //TODO implement login
        view.showSuccessFullSignIn()
    }

    override fun isAuthenticated() {
        firebaseAuth.addAuthStateListener(object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                if (firebaseAuth.currentUser != null) {
                    Log.d("isAuthenticated", "onAuthStateChanged:signed_in:" + firebaseAuth.currentUser!!.uid)
                    view.showSuccessFullSignIn()
                } else {
                    Log.d("isAuthenticated", "onAuthStateChanged:signed_out")
                }
            }
        });
    }


}