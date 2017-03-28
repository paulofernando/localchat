package site.paulo.localchat.ui.forecast

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_forecast.view.*
import site.paulo.localchat.R
import site.paulo.localchat.data.model.Forecast
import site.paulo.localchat.data.model.ForecastList
import site.paulo.localchat.ui.utils.ctx
import javax.inject.Inject

class ForecastAdapter
@Inject
constructor(var forecasts: ForecastList, val itemClick: (Forecast) -> Unit) : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_forecast, parent, false)
        return ForecastAdapter.ViewHolder(itemView, itemClick);
    }

    override fun onBindViewHolder(holder: ForecastAdapter.ViewHolder, position: Int) {
        holder.bindForecast(forecasts[position])
    }

    override fun getItemCount(): Int = forecasts.size()

    class ViewHolder(view: View, val itemClick: (Forecast) -> Unit) :
            RecyclerView.ViewHolder(view) {

        fun bindForecast(forecast: Forecast) {
            with(forecast) {
                Picasso.with(itemView.ctx).load(iconUrl).into(itemView.icon)
                itemView.date.text = date
                itemView.description.text = description
                itemView.maxTemperature.text = "${high.toString()}"
                itemView.minTemperature.text = "${low.toString()}"
                itemView.setOnClickListener { itemClick(this) }
            }
        }
    }

}
