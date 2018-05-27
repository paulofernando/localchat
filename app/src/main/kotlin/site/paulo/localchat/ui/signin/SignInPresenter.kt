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

package site.paulo.localchat.ui.signin

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import site.paulo.localchat.data.DataManager
import timber.log.Timber
import javax.inject.Inject


class SignInPresenter
@Inject
constructor(private val dataManager: DataManager, private val firebaseAuth: FirebaseAuth) : SignInContract.Presenter() {

    override fun signIn(email: String, password: String) {
        dataManager.authenticateUser(email, password)
            .subscribe({
                Timber.i("signIn: signInWithEmail:onComplete")
                view.showSuccessFullSignIn()
            }, {
                Timber.e("signIn: signInWithEmail:failed")
                view.showFailSignIn()
            })
    }

    override fun isAuthenticated() {
        firebaseAuth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                Timber.d("onAuthStateChanged:signed_in: %s", firebaseAuth.currentUser?.uid)
                try {
                    view.showSuccessFullSignIn()
                } catch (e: MvpViewNotAttachedException) {
                    Timber.e(e.message)
                }
            } else {
                Log.d("isAuthenticated", "onAuthStateChanged:signed_out")
            }
        }
    }


}