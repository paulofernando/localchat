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

package site.paulo.localchat.ui.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.FunctionSubscriber
import rx.lang.kotlin.addTo
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.manager.UserLocationManager
import site.paulo.localchat.data.model.firebase.NearbyUser
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.injection.ConfigPersistent
import site.paulo.localchat.ui.dashboard.nearby.UsersNearbyContract
import timber.log.Timber
import javax.inject.Inject

@ConfigPersistent
class UserNearbyPresenter
@Inject
constructor(private val dataManager: DataManager, private val firebaseAuth: FirebaseAuth,
    private val currentUser: CurrentUserManager) : UsersNearbyContract.Presenter() {

    override fun loadUsers() {
        //TODO change this method to listen for new user.
        dataManager.getUsers()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(FunctionSubscriber<List<User>>()
                .onNext {
                    val userEmail = firebaseAuth.currentUser?.email
                    if (it.isEmpty()) {
                        view.showNearbyUsersEmpty()
                    } else {
                        for (user in it) {
                            if (userEmail.equals(user.email)) {
                                //loaded current user data
                                currentUser.setUser(user)
                                UserLocationManager.instance.start()
                                listenNearbyUsers()
                                break
                            }
                        }
                    }
                }
                .onError {
                    Timber.e(it, "There was an error loading the users.")
                    view.showError()
                }
            ).addTo(compositeSubscription)
    }

    override fun listenNearbyUsers() {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val nearbyUser: NearbyUser = dataSnapshot.getValue(NearbyUser::class.java)
                if(!firebaseAuth.currentUser?.email.equals(nearbyUser.email)) //removing the current user from nearby users.
                    view.showNearbyUser(nearbyUser)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val nearbyUser: NearbyUser = dataSnapshot.getValue(NearbyUser::class.java)
                if(!firebaseAuth.currentUser?.email.equals(nearbyUser.email))
                    view.showNearbyUser(nearbyUser)
            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val nearbyUser: NearbyUser = dataSnapshot.getValue(NearbyUser::class.java)
                if(!firebaseAuth.currentUser?.email.equals(nearbyUser.email)) //removing the current user from nearby users.
                    view.removeNearbyUser(nearbyUser)
            }
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }


        dataManager.registerNewUsersChildEventListener(childEventListener)
        Timber.i("Listening for nearby users...")
    }

    private val compositeSubscription = CompositeSubscription()

    override fun detachView() {
        super.detachView()
        compositeSubscription.clear()
    }

}