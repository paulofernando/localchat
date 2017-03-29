package site.paulo.localchat.data.model.place

import com.google.gson.annotations.SerializedName
import java.util.*

data class Place(val id: String?,
                 val name: String,
                 val description: String,
                 var votesReceived: Int,
                 var votedByYou: Boolean,
                 val chosenThisWeek: Boolean,
                 @SerializedName("last_date_chosen") val lastDateChosen: Date?,
                 @SerializedName("votes_users") val usersWhoVoted: List<String>?) {

    constructor(id: String) : this(id, "", "", 0, false, false, Date(), emptyList())
}