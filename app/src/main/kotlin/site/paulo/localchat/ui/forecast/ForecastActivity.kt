package site.paulo.localchat.ui.forecast

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_forecast.*
import org.jetbrains.anko.toast
import site.paulo.localchat.R
import site.paulo.localchat.data.model.ForecastList
import site.paulo.localchat.ui.base.BaseActivity
import javax.inject.Inject

class ForecastActivity : BaseActivity(), ForecastContract.View {

        @Inject
        lateinit var presenter: ForecastPresenter

        @Inject
        lateinit var forecastsAdapter: ForecastAdapter

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            activityComponent.inject(this)
            ButterKnife.bind(this)
            setContentView(R.layout.activity_forecast)

            forecastList.adapter = forecastsAdapter
            forecastList.layoutManager = LinearLayoutManager(this)

            presenter.attachView(this)
            presenter.loadForecasts()

        }

    override fun showForecasts(list: ForecastList) {
        forecastsAdapter.forecasts = list
        forecastsAdapter.notifyDataSetChanged()
    }

    override fun showForecastEmpty() {
        //forecastsAdapter.forecasts = emptyList()
        forecastsAdapter.notifyDataSetChanged()
        toast(R.string.empty_ribots)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun showError() {
        toast(R.string.error_loading_ribots);
    }

}