package site.paulo.localchat.ui.settings.profile

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import site.paulo.localchat.R
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.ui.base.BaseActivity


class ProfileActivity: BaseActivity() {

    @BindView(R.id.toolbarProfile)
    lateinit var toolbar: Toolbar

    @BindView(R.id.profileProfileImg)
    lateinit var profileImg: ImageView

    @BindView(R.id.editProfileImageBtn)
    lateinit var changeImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //activityComponent.inject(this)
        setContentView(R.layout.activity_profile)
        ButterKnife.bind(this)

        toolbar.title = resources.getString(R.string.title_profile)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        var user: User = intent.getParcelableExtra<User>(resources.getString(R.string.user_name))
        var profilePic: Bitmap = intent.getParcelableExtra<Bitmap>(resources.getString(R.string.user_profile_image))

        profileImg.setImageBitmap(profilePic)
    }

    override fun onStart() {
        super.onStart()
        changeImage.postDelayed({ animateButton() }, 300)
    }

    override fun onBackPressed() {
        animateReverseButton()
        changeImage.postDelayed({ super.onBackPressed() }, 200)
    }

    fun animateButton() {
        changeImage.visibility = View.VISIBLE
        var myAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.profile_button_scale)
        changeImage.startAnimation(myAnim)
    }

    fun animateReverseButton() {
        var myAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.profile_button_scale_reverse)
        myAnim.setAnimationListener(object : AnimationListener {
            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                changeImage.visibility = View.INVISIBLE
            }
        })
        changeImage.startAnimation(myAnim)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
