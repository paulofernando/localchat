package site.paulo.localchat.data.model.forecast

data class City (val id: Int,
                 val name: String,
                 val coord: Coord,
                 val country: String,
                 val population: Int,
                 val sys: Sys)