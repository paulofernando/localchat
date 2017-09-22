package site.paulo.localchat.ui.about

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.ctx

import site.paulo.localchat.R

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        appNameAboutTxt.text = ctx.getApplicationInfo().loadLabel(ctx.getPackageManager())
    }
}
