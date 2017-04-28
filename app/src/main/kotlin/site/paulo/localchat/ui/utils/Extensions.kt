package site.paulo.localchat.ui.utils

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import site.paulo.localchat.R
import java.util.*

fun Date.formattedTime(context: Context, time: Long): String {
    val cal = Calendar.getInstance(Locale.ENGLISH)
    cal.timeInMillis = time

    var date: String?
    if (DateUtils.isToday(time)) {
        date = context.resources.getString(R.string.lb_today) + DateFormat.format(", kk:mm", cal).toString()
    } else {
        date = DateFormat.format("d MMM, kk:mm", cal).toString()
    }
    return date
}
