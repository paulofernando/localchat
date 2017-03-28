package site.paulo.localchat.data.model

import nz.bradcampbell.paperparcel.PaperParcel

@PaperParcel
data class Forecast(val date: String,
                    val description: String,
                    val high: Int,
                    val low: Int,
                    val iconUrl: String)

@PaperParcel
data class ForecastList(val city: String, val country: String, val dailyForecast:List<Forecast>) {
        operator fun get(position: Int): Forecast = dailyForecast[position]
        fun size(): Int = dailyForecast.size;
        fun isEmpty(): Boolean = dailyForecast.size == 0;
}