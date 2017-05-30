package site.paulo.localchat.ui.signup

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.Executors.newSingleThreadExecutor
import javax.inject.Inject

class SignUpPresenter
@Inject
constructor(private val firebaseAuth: FirebaseAuth, private val firebase: FirebaseDatabase)
    : SignUpContract.Presenter() {

    override fun signUp(email: String, password: String, name: String, age: Long, gender: String) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(newSingleThreadExecutor(), object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    Log.d("signUp", "createUserWithEmail:onComplete:" + task.isSuccessful)

                    if (!task.isSuccessful) {
                        println("Sign up failed")
                    } else {
                        println("Signed up")
                        registerUser(email, name, age, gender);
                        view.showSuccessFullSignUp()
                    }
                }

            })
    }

    private fun registerUser(email: String, name: String, age: Long, gender: String) {
        val value = mutableMapOf<String, Any>()
        value.put("email", email)
        value.put("name", name)
        value.put("age", age)
        value.put("gender", gender)
        firebase.getReference("users").push().setValue(value)
    }
}