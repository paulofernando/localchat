package site.paulo.localchat.ui.forecast

import site.paulo.localchat.data.model.forecast.ForecastList
import site.paulo.localchat.data.model.ribot.Ribot
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object ForecastContract {

    interface View: MvpView {
        fun showForecasts(forecasts: ForecastList)
        fun showForecastsEmpty()
        fun showError()
    }

    abstract class Presenter: BaseMvpPresenter<View>() {
        abstract fun loadForecasts()
    }
}
