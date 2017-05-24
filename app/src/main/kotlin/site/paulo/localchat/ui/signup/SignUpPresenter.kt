package site.paulo.localchat.ui.signup

import android.app.Activity
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import site.paulo.localchat.R
import site.paulo.localchat.ui.main.MainActivity
import java.util.concurrent.Executors
import java.util.concurrent.Executors.*
import javax.inject.Inject

class SignUpPresenter
@Inject
constructor(private val firebaseAuth: FirebaseAuth) : SignUpContract.Presenter() {

    override fun signUp(email: String, password: String, name: String, age: String, gender: String) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(newSingleThreadExecutor(), object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    Log.d("signUp", "createUserWithEmail:onComplete:" + task.isSuccessful)

                    if (!task.isSuccessful) {
                        println("Sign up failed")
                    } else {
                        println("Signed up")

                        //TODO implement sign up
                        view.showSuccessFullSignUp()
                    }
                }

            })

    }
}