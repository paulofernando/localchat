package site.paulo.localchat.ui.signup

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.model.chatgeo.User
import java.util.concurrent.Executors.newSingleThreadExecutor
import javax.inject.Inject

class SignUpPresenter
@Inject
constructor(private val dataManager: DataManager, private val firebaseAuth: FirebaseAuth)
    : SignUpContract.Presenter() {

    override fun signUp(email: String, password: String, name: String, age: Long, gender: String) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(newSingleThreadExecutor(), object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    Log.d("signUp", "createUserWithEmail:onComplete:" + task.isSuccessful)

                    if (!task.isSuccessful) {
                        println("Sign up failed")
                        view.showFailSignUp()
                    } else {
                        println("Signed up")
                        var user:User = User(name,age,email,gender)
                        registerUser(user)
                        view.showSuccessFullSignUp()
                    }
                }
            })
    }

    private fun registerUser(user: User) {
        dataManager.registerUser(user)
    }
}