package site.paulo.localchat.ui.signin

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executors
import javax.inject.Inject

class SignInPresenter
@Inject
constructor(private val firebaseAuth: FirebaseAuth) : SignInContract.Presenter() {

    override fun signIn(email: String, password: String) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(Executors.newSingleThreadExecutor(), object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    Log.d("signIn", "signInWithEmail:onComplete:" + task.isSuccessful)

                    if (!task.isSuccessful) {
                        Log.d("signIn", "signInWithEmail:failed:" + task.exception)
                        view.showFailSignIn()
                    } else {
                        view.showSuccessFullSignIn()
                    }
                }
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