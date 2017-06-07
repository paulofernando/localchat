package site.paulo.localchat.ui.settings.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import com.squareup.picasso.Picasso
import org.jetbrains.anko.ctx
import site.paulo.localchat.R
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.utils.CircleTransform
import android.provider.MediaStore
import java.io.ByteArrayOutputStream


class ProfileActivity: BaseActivity() {

    @BindView(R.id.toolbarProfile)
    lateinit var toolbar: Toolbar

    @BindView(R.id.profileProfileImg)
    lateinit var profileImg: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //activityComponent.inject(this)
        setContentView(R.layout.activity_profile)
        ButterKnife.bind(this)

        toolbar.title = resources.getString(R.string.title_settings)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        var user: User = intent.getParcelableExtra<User>(resources.getString(R.string.user_name))
        var profilePic: Bitmap = intent.getParcelableExtra<Bitmap>(resources.getString(R.string.user_profile_image))

        //TODO send the image data directly from the Settings Activity
        profileImg.setImageBitmap(profilePic);


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
