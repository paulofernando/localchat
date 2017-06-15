package site.paulo.localchat.ui.empty

import android.content.Intent
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.signin.SignInActivity


class MainEmptyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityIntent: Intent

        //if (Util.getToken() != null) {
        //activityIntent = Intent(this, DashboardActivity::class.java)
        //} else {
        activityIntent = Intent(this, SignInActivity::class.java)
        //}

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        startActivity(activityIntent)
        finish()

    }

}
