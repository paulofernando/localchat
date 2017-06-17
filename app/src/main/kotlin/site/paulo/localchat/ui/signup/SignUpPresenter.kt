/*
 * Copyright 2017 Paulo Fernando
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package site.paulo.localchat.ui.signup

import com.google.firebase.auth.FirebaseAuth
import com.kelvinapps.rxfirebase.RxFirebaseAuth
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.model.firebase.User
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