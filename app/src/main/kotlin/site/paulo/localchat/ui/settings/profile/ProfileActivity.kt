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
import site.paulo.localchat.data.remote.FirebaseHelper
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.settings.ProfilePresenter
import javax.inject.Inject


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

    @BindView(R.id.ageUserProfileLabel) lateinit var ageLb: TextView
    @BindView(R.id.ageUserProfileTxt) lateinit var ageTxt: EditText
    @BindView(R.id.ageUserEditProfileImg) lateinit var ageEditImg: ImageView
    @BindView(R.id.ageUserCancelProfileImg) lateinit var ageCancelImg: ImageView
    @BindView(R.id.ageUserConfirmProfileImg) lateinit var ageConfirmImg: ImageView
    @BindView(R.id.ageUserEditProfileContainer) lateinit var ageEditContainer: LinearLayout

    @BindView(R.id.genderUserProfileLabel)
    lateinit var genderLb: TextView

    lateinit var user: User

    @Inject
    lateinit var presenter: ProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
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
        ageLb.text = user.age.toString()
        ageTxt.setText(user.age.toString(), TextView.BufferType.EDITABLE)
        emailLb.text = user.email
        genderLb.text = user.gender
    }

    /**************** Name *********************/

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
        val name = nameTxt.text.toString()
        if (name.isEmpty() || name.length < 6) {
            nameTxt.error = resources.getString(R.string.error_chars_least_number)
        } else {
            nameTxt.error = null
            nameLb.text = nameTxt.text
            presenter.updateUserData(FirebaseHelper.Companion.UserDataType.NAME, nameTxt.text.toString())
            hideNameConfirmationButton()
        }
    }

    fun hideNameConfirmationButton() {
        nameLb.visibility = View.VISIBLE
        nameEditImg.visibility = View.VISIBLE
        nameTxt.visibility = View.GONE
        nameEditContainer.visibility = View.GONE
    }

    /**************** Age *********************/

    override fun editAge(view:View) {
        ageLb.visibility = View.GONE
        ageEditImg.visibility = View.GONE
        ageTxt.visibility = View.VISIBLE
        ageEditContainer.visibility = View.VISIBLE
    }

    override fun cancelAgeEdition(view:View) {
        hideAgeConfirmationButton()
    }

    override fun confirmAgeEdition(view:View) {
        val age = ageTxt.text.toString().toLong()
        if (ageTxt.text.isEmpty() || age !in 17..100) {
            ageTxt.error = resources.getString(R.string.error_age_bet_numbers)
        } else {
            ageTxt.error = null
            ageLb.text = ageTxt.text
            presenter.updateUserData(FirebaseHelper.Companion.UserDataType.AGE, ageTxt.text.toString())
            hideAgeConfirmationButton()
        }
    }

    fun hideAgeConfirmationButton() {
        ageLb.visibility = View.VISIBLE
        ageEditImg.visibility = View.VISIBLE
        ageTxt.visibility = View.GONE
        ageEditContainer.visibility = View.GONE
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
