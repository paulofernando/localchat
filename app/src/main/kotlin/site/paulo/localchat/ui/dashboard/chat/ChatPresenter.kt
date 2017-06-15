package site.paulo.localchat.ui.user

import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.FunctionSubscriber
import rx.lang.kotlin.addTo
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.injection.ConfigPersistent
import site.paulo.localchat.ui.dashboard.nearby.ChatContract
import timber.log.Timber
import javax.inject.Inject


@ConfigPersistent
class ChatPresenter
@Inject
constructor(private val dataManager: DataManager) : ChatContract.Presenter() {

    override fun loadChatRooms(userId:String) {

        dataManager.getUser(userId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(FunctionSubscriber<User>()
                .onNext {
                    if(it.chats.isEmpty()) {
                        view.showChatsEmpty()
                    } else {
                        it.chats.forEach {
                            loadChatRoom(it.value)
                        }
                    }
                }
                .onError {
                    view.showError()
                    Timber.e(it, "There was an error loading chats from an user.")
                }
            ).addTo(compositeSubscription)

    }

    override fun loadChatRoom(chatId: String) {
        dataManager.getChatRoom(chatId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(FunctionSubscriber<Chat>()
                .onNext {
                    view.showChat(it)
                }
                .onError {
                    Timber.e(it, "There was an error loading a chat room.")
                    view.showError()
                }
            ).addTo(compositeSubscription)
    }

    override fun loadProfilePicture(chatList: List<Chat>) {
        //TODO
    }

    private val compositeSubscription = CompositeSubscription()

    override fun detachView() {
        super.detachView()
        compositeSubscription.clear()
    }

}