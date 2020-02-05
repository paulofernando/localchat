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

import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.LocalDataManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.ui.utils.Utils
import site.paulo.localchat.ui.utils.getFirebaseId
import timber.log.Timber
import javax.inject.Inject

class SignInPresenter
@Inject
constructor(private val dataManager: DataManager,
            private val firebaseAuth: FirebaseAuth,
            private val currentUserManager: CurrentUserManager,
            private val localDataManager: LocalDataManager) : SignInContract.Presenter() {

    override fun signIn(email: String, password: String) {
        dataManager.authenticateUser(email, password)
            .subscribe({
                Timber.i("signIn: signInWithEmail:onComplete")
            }, {
                Timber.e("signIn: signInWithEmail:failed")
                view.showFailSignIn()
            })
    }

    override fun isAuthenticated(callNext: (() -> Unit)) {
        firebaseAuth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                Timber.d("onAuthStateChanged:signed_in: %s", firebaseAuth.currentUser?.uid)
                try {
                    if (currentUserManager.hasUser()) {
                        currentUserManager.setUserByEmail(firebaseAuth.currentUser?.email, localDataManager)
                        view.showSuccessFullSignIn()
                    } else {
                        dataManager.getUser(Utils.getFirebaseId(firebaseAuth.currentUser?.email ?: "")).toObservable()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribeBy(onNext = {
                                    currentUserManager.setUser(it, localDataManager)
                                    view.showSuccessFullSignIn()
                                }, onError = {
                                    Timber.e(it, "There was an error loading current user.")
                                }).addTo(compositeDisposable)
                    }
                } catch (e: MvpViewNotAttachedException) {
                    Timber.e(e)
                }
            } else {
                callNext.invoke()
                Timber.d("onAuthStateChanged:signed_out")
            }
        }

    }

    private val compositeDisposable = CompositeDisposable()

    override fun detachView() {
        super.detachView()
        compositeDisposable.clear()
    }


}