package site.paulo.localchat.ui.forecast

import site.paulo.localchat.data.model.ForecastList
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object ForecastContract {

    interface View: MvpView {
        fun showForecasts(list: ForecastList)
        fun showForecastEmpty()
        fun showError()
    }

    abstract class Presenter: BaseMvpPresenter<View>() {
        abstract fun loadForecasts()
    }
}
