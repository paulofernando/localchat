package site.paulo.localchat.ui.main

import javax.inject.Inject

import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.FunctionSubscriber
import rx.lang.kotlin.addTo
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.model.ribot.Ribot
import site.paulo.localchat.injection.ConfigPersistent

@ConfigPersistent
class MainPresenter
@Inject
constructor(private val dataManager: DataManager) : MainContract.Presenter() {

    private val compositeSubscription = CompositeSubscription()

    override fun detachView() {
        super.detachView()
        compositeSubscription.clear()
    }

    override fun loadRibots() {
        dataManager.getRibots()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(FunctionSubscriber<List<Ribot>>()
                        .onNext {
                            if (it.isEmpty()) view.showRibotsEmpty() else view.showRibots(it)
                        }
                        .onError {
                            Timber.e(it, "There was an error loading the ribots.")
                            view.showError()
                        }
                ).addTo(compositeSubscription)
    }

}
