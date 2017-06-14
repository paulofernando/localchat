package site.paulo.localchat.ui.user

import com.google.firebase.auth.FirebaseAuth
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.FunctionSubscriber
import rx.lang.kotlin.addTo
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.manager.CurrentUserManager
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

    override fun loadNearbyUsers() {
        dataManager.getUsers()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(FunctionSubscriber<List<User>>()
                .onNext {
                    val userEmail = firebaseAuth.currentUser?.email
                    var userList = mutableListOf<User>()
                    it.forEach {
                        if(!userEmail.equals(it.email)) {
                            userList.add(it)
                        } else {
                            currentUser.setUser(it)
                        }
                    } //removing the current user from nearby users.

                    if (it.isEmpty()) view.showNearbyUsersEmpty() else view.showNearbyUsers(userList.toList())
                }
                .onError {
                    Timber.e(it, "There was an error loading the users.")
                    view.showError()
                }
            ).addTo(compositeSubscription)
    }

    private val compositeSubscription = CompositeSubscription()

    override fun detachView() {
        super.detachView()
        compositeSubscription.clear()
    }

}