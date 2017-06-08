package site.paulo.localchat.ui.settings

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.transition.Transition
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.squareup.picasso.Picasso
import org.jetbrains.anko.ctx
import site.paulo.localchat.R
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.settings.profile.ProfileActivity
import site.paulo.localchat.ui.utils.CircleTransform
import javax.inject.Inject


class SettingsActivity: BaseActivity(), SettingsContract.View   {

    @BindView(R.id.toolbarSettings)
    lateinit var toolbar: Toolbar

    @BindView(R.id.profileContainer)
    lateinit var profileContainer:RelativeLayout

    @BindView(R.id.nameSettingsTxt)
    lateinit var profileName:TextView

    @BindView(R.id.profileSettingsImg)
    lateinit var profileImage: ImageView

    private var user:User? = null

    @Inject
    lateinit var presenter: SettingsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        setContentView(R.layout.activity_settings)
        ButterKnife.bind(this)

        presenter.attachView(this)

        toolbar.title = resources.getString(R.string.title_settings)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        profileContainer.setOnClickListener {
            launchProfileEditor()
        }

        presenter.loadCurrentUser()

    }

    fun launchProfileEditor() {
        val intent = Intent(this, ProfileActivity::class.java)

        intent.putExtra(getString(R.string.user_name), user)
        intent.putExtra(getString(R.string.user_profile_image), (profileImage.drawable as BitmapDrawable).bitmap)
        val transitionName = getString(R.string.profile_image_name)
        val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this, profileImage, transitionName)

        startActivity(intent, transitionActivityOptions.toBundle())
    }

    override fun showCurrentUserData(user: User) {
        this.user = user
        Picasso.with(ctx)
            .load(user.profilePic)
            .resize(ctx.resources.getDimension(R.dimen.image_width_settings).toInt(),
                ctx.resources.getDimension(R.dimen.image_height_settings).toInt())
            .centerCrop()
            .transform(CircleTransform())
            .into(profileImage)
        profileName.text = user.name
    }


}
