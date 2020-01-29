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

package site.paulo.localchat.ui.dashboard.nearby

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.manager.UserLocationManager
import site.paulo.localchat.data.model.firebase.NearbyUser
import site.paulo.localchat.injection.ConfigPersistent
import timber.log.Timber
import javax.inject.Inject

@ConfigPersistent
class UserNearbyPresenter
@Inject
constructor(private val dataManager: DataManager, private val firebaseAuth: FirebaseAuth,
    private val currentUser: CurrentUserManager) : UsersNearbyContract.Presenter() {

    override fun loadUsers(callback: (() -> Unit)?) {
        //TODO change this method to listen for new user.
        dataManager.getUsers().toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(onNext = {
                    val userEmail = firebaseAuth.currentUser?.email
                    if (it.isEmpty()) {
                        view.showNearbyUsersEmpty()
                    } else {
                        for (user in it) {
                            if (userEmail.equals(user.email)) {
                                //loaded current user data
                                currentUser.setUser(user)
                                UserLocationManager.instance.start({listenNearbyUsers()})
                                break
                            }
                        }
                    }
                }, onComplete = {
                    callback?.invoke()
                }, onError = {
                    Timber.e(it, "There was an error loading the users.")
                    view.showError()
                }
            ).addTo(compositeDisposable)
    }

    override fun listenNearbyUsers() {
        val childEventListener = NearbyUserChildEventListener(firebaseAuth.currentUser, this)
        dataManager.registerNewUsersChildEventListener(childEventListener)
        Timber.i("Listening for nearby users...")
    }

    private val compositeDisposable = CompositeDisposable()

    override fun detachView() {
        super.detachView()
        compositeDisposable.clear()
    }

    private class NearbyUserChildEventListener
    constructor(private val firebaseUser: FirebaseUser?,
                private val presenter: UserNearbyPresenter) : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val nearbyUser: NearbyUser = dataSnapshot.getValue(NearbyUser::class.java)!!
            if (!firebaseUser?.email.equals(nearbyUser.email) && nearbyUser.email.isNotEmpty()) //removing the current user from nearby users.
                presenter.view.showNearbyUser(nearbyUser)
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
            val nearbyUser: NearbyUser = dataSnapshot.getValue(NearbyUser::class.java)!!
            if (!firebaseUser?.email.equals(nearbyUser.email))
                presenter.view.showNearbyUser(nearbyUser)
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            val nearbyUser: NearbyUser = dataSnapshot.getValue(NearbyUser::class.java)!!
            if (!firebaseUser?.email.equals(nearbyUser.email)) //removing the current user from nearby users.
                presenter.view.removeNearbyUser(nearbyUser)
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onCancelled(databaseError: DatabaseError) {}
    }

}