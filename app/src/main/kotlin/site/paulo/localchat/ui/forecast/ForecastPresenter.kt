package site.paulo.localchat.ui.forecast

import android.text.TextUtils.isEmpty
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.FunctionSubscriber
import rx.lang.kotlin.addTo
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.data.model.forecast.ForecastList
import site.paulo.localchat.data.model.ribot.Ribot
import site.paulo.localchat.injection.ConfigPersistent
import timber.log.Timber
import javax.inject.Inject

@ConfigPersistent
class ForecastPresenter
@Inject
constructor(private val dataManager: DataManager) : ForecastContract.Presenter() {

    override fun loadForecasts() {
        /*dataManager.getForecasts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(FunctionSubscriber<ForecastList>()
                        .onNext {
                            println(it.name);
                        }
                        .onError {
                            Timber.e(it, "There was an error loading the ribots.")
                            view.showError()
                        }
                ).addTo(compositeSubscription)*/
        /*dataManager.getPlaces()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                }
                .subscribe({ places ->
                    println(places);
                }, { error ->
                    println(error);
                })*/

        dataManager.getUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(FunctionSubscriber<List<User>>()
                        .onNext {
                            //println(it.toString());
                            if (it.isEmpty()) view.showForecastsEmpty() else view.showForecasts(it)
                        }
                        .onError {
                            Timber.e(it, "There was an error loading the users.")
                            view.showError()
                        }
                ).addTo(compositeSubscription)

        /*dataManager.getWeatherInfo("27.6988910", "84.430396084", "10", "b1b15e88fa797225412429c1c50c122a1")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                }
                .subscribe({ forecast ->
                    println(forecast);
                }, { error ->
                    println(error);
                })*/
    }

    private val compositeSubscription = CompositeSubscription()

    override fun detachView() {
        super.detachView()
        compositeSubscription.clear()
    }

}