package site.paulo.localchat.data.model.forecast

import java.util.*

data class WeatherData(val city: City? = null,
                       val cod: String? = null,
                       val message: String? = null,
                       val cnt: Int? = null,
                       val list: ArrayList<Any> = ArrayList<Any>())