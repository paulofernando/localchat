package site.paulo.localchat.ui.forecast

import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import site.paulo.localchat.R
import site.paulo.localchat.data.model.forecast.ForecastList
import site.paulo.localchat.data.model.ribot.Ribot
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.main.ForecastAdapter
import site.paulo.localchat.ui.main.RibotsAdapter
import javax.inject.Inject

class ForecastActivity : BaseActivity(), ForecastContract.View {

    @Inject
    lateinit var presenter: ForecastPresenter

    @Inject
    lateinit var forecastsAdapter: ForecastAdapter

    @BindView(R.id.forecastList)
    lateinit var forecastList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        setContentView(R.layout.activity_forecast)
        ButterKnife.bind(this)

        forecastList.adapter = forecastsAdapter
        forecastList.layoutManager = LinearLayoutManager(this)

        presenter.attachView(this)
        presenter.loadForecasts()

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun showForecasts(f: ForecastList) {
        forecastsAdapter.forecasts = listOf(f) //TODO change later
        forecastsAdapter.notifyDataSetChanged()
    }

    override fun showForecastsEmpty() {
        forecastsAdapter.forecasts = emptyList()
        forecastsAdapter.notifyDataSetChanged()
        Toast.makeText(this, R.string.empty_ribots, Toast.LENGTH_LONG).show()
    }

    override fun showError() {
        Toast.makeText(this, R.string.error_loading_ribots, Toast.LENGTH_LONG).show()
    }

}