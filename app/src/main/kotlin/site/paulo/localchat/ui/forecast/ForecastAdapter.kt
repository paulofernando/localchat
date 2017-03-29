package site.paulo.localchat.ui.main

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import javax.inject.Inject

import butterknife.BindView
import butterknife.ButterKnife

import site.paulo.localchat.data.model.ribot.Ribot
import site.paulo.localchat.R
import site.paulo.localchat.data.model.forecast.ForecastList

class ForecastAdapter
@Inject
constructor() : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    var forecasts = emptyList<ForecastList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastAdapter.ForecastViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ForecastAdapter.ForecastViewHolder, position: Int) {
        val forecast = forecasts[position]

        holder.dateTextView.text = forecast.name;
    }

    override fun getItemCount(): Int {
        return forecasts.size
    }

    inner class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.description)
        lateinit var descriptionTextView: View

        @BindView(R.id.date)
        lateinit var dateTextView: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
