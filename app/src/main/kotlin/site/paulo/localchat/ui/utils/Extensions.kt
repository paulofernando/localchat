package site.paulo.localchat.ui.utils

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import kotlinx.android.synthetic.main.item_user.view.*
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


fun ImageView.loadUrl(url: String) {
    Picasso.with(context).load(url).into(this)
}

fun ImageView.loadUrl(url: String, request: (RequestCreator) -> RequestCreator) {
    request(Picasso.with(context).load(url)).into(this)
}

fun ImageView.loadUrlAndResize(url: String?, resize: Int) {
    Picasso.with(context).load(url).resize(resize, resize).centerCrop().into(this)
}

fun ImageView.loadUrlAndResize(url: String?, resize: Int, request: (RequestCreator) -> RequestCreator) {
    request(Picasso.with(context).load(url).resize(resize, resize).centerCrop()).into(this)
}

fun ImageView.loadResourceAndResize(resource: Int, resize: Int) {
    Picasso.with(context).load(resource).resize(resize, resize).centerCrop().into(this)
}

fun ImageView.loadResourceAndResize(resource: Int, resize: Int, request: (RequestCreator) -> RequestCreator) {
    request(Picasso.with(context).load(resource).resize(resize, resize).centerCrop()).into(this)
}
