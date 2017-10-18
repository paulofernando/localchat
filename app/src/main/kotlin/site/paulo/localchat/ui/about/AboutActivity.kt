package site.paulo.localchat.ui.about

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.ctx

import site.paulo.localchat.R
import android.content.pm.PackageManager
import android.R.attr.versionName
import android.content.pm.PackageInfo



class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        appNameAboutTxt.text = ctx.getApplicationInfo().loadLabel(ctx.packageManager)

        try {
            val pInfo = this.packageManager.getPackageInfo(packageName, 0)
            appVersionAboutTxt.text = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }
}
