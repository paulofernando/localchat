package site.paulo.localchat.ui.forecast

import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.FunctionSubscriber
import rx.lang.kotlin.addTo
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.model.ForecastList
import site.paulo.localchat.injection.ConfigPersistent
import timber.log.Timber
import javax.inject.Inject

@ConfigPersistent
class ForecastPresenter
@Inject
constructor(private val dataManager: DataManager) : ForecastContract.Presenter() {


    private val compositeSubscription = CompositeSubscription()

    override fun detachView() {
        super.detachView()
        compositeSubscription.clear()
    }

    override fun loadForecasts() {
        dataManager.getForecasts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(FunctionSubscriber<ForecastList>()
                        .onNext {
                            if (it.isEmpty()) view.showForecastEmpty() else view.showForecasts(it)
                        }
                        .onError {
                            Timber.e(it, "There was an error loading the forecasts.")
                            view.showError()
                        }
                ).addTo(compositeSubscription)
    }

}