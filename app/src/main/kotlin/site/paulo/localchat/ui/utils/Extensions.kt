package site.paulo.localchat.ui.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.view.PixelCopy.request
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
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

/* ---------------------- ImageView ---------------------------*/

fun ImageView.loadUrlAndCacheOffline(url: String) {
    val imageView:ImageView  = this
    Picasso.get().load(url).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, object: Callback {
        override fun onSuccess() { }
        override fun onError(e:Exception) {
            imageView.loadUrl(url)
        }
    })
}

fun ImageView.loadUrl(url: String?) {
    if(url != null) Picasso.get().load(url).into(this)
}

fun ImageView.loadUrl(url: String?, request: (RequestCreator) -> RequestCreator) {
    if(url != null)
        request(Picasso.get().load(url)).placeholder(R.drawable.empty).into(this)
}

/**
 * The same as loadUrl but with a circle image placeholder
 */
fun ImageView.loadUrlCircle(url: String?, request: (RequestCreator) -> RequestCreator) {
    if(url != null)
        request(Picasso.get().load(url)).placeholder(R.drawable.empty_circle).into(this)
}

fun ImageView.loadUrlCircle(url: String?, callback: Callback, request: (RequestCreator) -> RequestCreator) {
    if (url != null)
        request(Picasso.get().load(url)).placeholder(R.drawable.empty_circle).into(this, callback)
}

fun ImageView.loadUrlAndResize(url: String?, resize: Int) {
    if (url != null)
        Picasso.get().load(url).placeholder(R.drawable.empty).resize(resize, resize).centerCrop().into(this)
}

fun ImageView.loadUrlAndResize(url: String?, resize: Int, request: (RequestCreator) -> RequestCreator) {
    if(url != null)
        request(Picasso.get().load(url).placeholder(R.drawable.empty).resize(resize, resize).centerCrop()).into(this)
}

/**
 * The same as loadUrlAndResize but with a circle image placeholder
 */
fun ImageView.loadUrlAndResizeCircle(url: String?, resize: Int, request: (RequestCreator) -> RequestCreator) {
    if (url != null)
        request(Picasso.get().load(url).placeholder(R.drawable.empty_circle).resize(resize, resize).centerCrop()).into(this)
}

fun ImageView.loadUrlAndResizeCirclePlaceholder(url: String?, resize: Int, placeholder: Drawable, request: (RequestCreator) -> RequestCreator) {
    if (url != null)
        request(Picasso.get().load(url).placeholder(placeholder).resize(resize, resize).centerCrop()).into(this)

}

fun ImageView.loadResourceAndResize(resource: Int, resize: Int) {
    Picasso.get().load(resource).resize(resize, resize).centerCrop().into(this)
}

fun ImageView.loadResourceAndResize(resource: Int, resize: Int, request: (RequestCreator) -> RequestCreator) {
    request(Picasso.get().load(resource).resize(resize, resize).centerCrop()).into(this)
}
