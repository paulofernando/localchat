package site.paulo.localchat.ui.settings.profile

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import site.paulo.localchat.R
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.ui.base.BaseActivity


class ProfileActivity: BaseActivity(), ProfileContract.View {

    @BindView(R.id.toolbarProfile)
    lateinit var toolbar: Toolbar

    @BindView(R.id.profileProfileImg)
    lateinit var profileImg: ImageView

    @BindView(R.id.editProfileImageBtn)
    lateinit var changeImage: ImageView

    @BindView(R.id.nameUserProfileLabel) lateinit var nameLb: TextView
    @BindView(R.id.nameUserProfileTxt) lateinit var nameTxt: EditText
    @BindView(R.id.nameUserEditProfileImg) lateinit var nameEditImg: ImageView
    @BindView(R.id.nameUserCancelProfileImg) lateinit var nameCancelImg: ImageView
    @BindView(R.id.nameUserConfirmProfileImg) lateinit var nameConfirmImg: ImageView
    @BindView(R.id.nameUserEditProfileContainer) lateinit var nameEditContainer: LinearLayout

    @BindView(R.id.emailUserProfileLabel)
    lateinit var emailLb: TextView

    @BindView(R.id.ageUserProfileLabel)
    lateinit var ageLb: TextView

    @BindView(R.id.genderUserProfileLabel)
    lateinit var genderLb: TextView

    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //activityComponent.inject(this)
        setContentView(R.layout.activity_profile)
        ButterKnife.bind(this)

        toolbar.title = resources.getString(R.string.title_profile)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        user = intent.getParcelableExtra<User>(resources.getString(R.string.user_name))
        var profilePic: Bitmap = intent.getParcelableExtra<Bitmap>(resources.getString(R.string.user_profile_image))

        profileImg.setImageBitmap(profilePic)
        showCurrentUserData()
    }

    override fun showCurrentUserData() {
        nameLb.text = user.name
        nameTxt.setText(user.name, TextView.BufferType.EDITABLE)
        ageLb.text = user.age.toString() + " yo"
        emailLb.text = user.email
        genderLb.text = user.gender
    }

    override fun editName(view:View) {
        nameLb.visibility = View.GONE
        nameEditImg.visibility = View.GONE
        nameTxt.visibility = View.VISIBLE
        nameEditContainer.visibility = View.VISIBLE
    }

    override fun cancelNameEdition(view:View) {
        hideNameConfirmationButton()
    }

    override fun confirmNameEdition(view:View) {
        nameLb.text = nameTxt.text
        hideNameConfirmationButton()
    }

    fun hideNameConfirmationButton() {
        nameLb.visibility = View.VISIBLE
        nameEditImg.visibility = View.VISIBLE
        nameTxt.visibility = View.GONE
        nameEditContainer.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        changeImage.postDelayed({ animateButton() }, 300)
    }

    override fun onBackPressed() {
        animateReverseButton()
        changeImage.postDelayed({ super.onBackPressed() }, 200)
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



}
