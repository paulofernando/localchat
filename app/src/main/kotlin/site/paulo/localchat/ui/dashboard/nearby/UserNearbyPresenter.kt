package site.paulo.localchat.ui.user

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.FunctionSubscriber
import rx.lang.kotlin.addTo
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.model.chatgeo.ChatMessage
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.injection.ConfigPersistent
import site.paulo.localchat.ui.dashboard.nearby.UsersNearbyContract
import timber.log.Timber
import javax.inject.Inject

@ConfigPersistent
class UserNearbyPresenter
@Inject
constructor(private val dataManager: DataManager, private val firebase: FirebaseDatabase) : UsersNearbyContract.Presenter() {

    override fun loadNearbyUsers() {
        dataManager.getUsers()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(FunctionSubscriber<List<User>>()
                .onNext {
                    //println(it.toString());
                    if (it.isEmpty()) view.showNearbyUsersEmpty() else view.showNearbyUsers(it)
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