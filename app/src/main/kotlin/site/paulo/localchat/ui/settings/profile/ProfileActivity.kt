/*
 * Copyright 2017 Paulo Fernando
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package site.paulo.localchat.ui.settings.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.squareup.picasso.Callback
import site.paulo.localchat.R
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.data.remote.FirebaseHelper
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.settings.ProfilePresenter
import site.paulo.localchat.ui.utils.CircleTransform
import site.paulo.localchat.ui.utils.loadUrlCircle
import javax.inject.Inject


class ProfileActivity : BaseActivity(), ProfileContract.View {

    internal val RC_PHOTO_PICKER = 1

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

    @BindView(R.id.loadingProfileProgress)
    lateinit var loadingProfileProgress: ProgressBar

    lateinit var user: User

    var lastValue: String? = null

    @Inject
    lateinit var presenter: ProfilePresenter

    @Inject
    lateinit var currentUserManager: CurrentUserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        presenter.attachView(this)
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

        when (user.gender) {
            "m" -> genderLb.text = resources.getString(R.string.lb_gender_male)
            "f" -> genderLb.text = resources.getString(R.string.lb_gender_female)
        }

    }

    /**************** Name *********************/

    override fun editName(view: View) {
        cancelAgeEdition(ageTxt)
        nameLb.visibility = View.GONE
        nameEditImg.visibility = View.GONE
        nameTxt.visibility = View.VISIBLE
        nameEditContainer.visibility = View.VISIBLE
        lastValue = nameLb.text.toString()
    }

    override fun cancelNameEdition(view: View) {
        hideNameConfirmationButton()
    }

    override fun confirmNameEdition(view: View) {
        val name = nameTxt.text.toString()
        val minimumCharsName: Int = resources.getString(R.string.number_minimum_chars_user_name).toInt()
        val maximumCharsName: Int = resources.getString(R.string.number_maximum_chars_user_name).toInt()
        if (name.isEmpty() || name.length < minimumCharsName || name.length > maximumCharsName) {
            nameTxt.error = resources.getString(R.string.error_chars_bet_numbers, minimumCharsName, maximumCharsName)
        } else {
            nameTxt.error = null
            if (!lastValue!!.equals(nameTxt.text.toString())) {
                nameLb.text = nameTxt.text
                presenter.updateUserData(FirebaseHelper.Companion.UserDataType.NAME, nameTxt.text.toString())
            }
            hideNameConfirmationButton()
        }
    }

    /**************** Age *********************/

    override fun editAge(view: View) {
        cancelAgeEdition(nameTxt)
        ageLb.visibility = View.GONE
        ageEditImg.visibility = View.GONE
        ageTxt.visibility = View.VISIBLE
        ageEditContainer.visibility = View.VISIBLE
        lastValue = nameLb.text.toString()
    }

    override fun cancelAgeEdition(view: View) {
        hideAgeConfirmationButton()
    }

    override fun confirmAgeEdition(view: View) {
        val age = ageTxt.text.toString().toLong()
        val minimumAge: Int = resources.getString(R.string.number_minimum_age).toInt()
        val maximumAge: Int = resources.getString(R.string.number_maximum_age).toInt()
        if (ageTxt.text.isEmpty() || age !in (minimumAge - 1)..(maximumAge + 1)) {
            ageTxt.error = resources.getString(R.string.error_numbers_bet, minimumAge, maximumAge)
        } else {
            ageTxt.error = null
            if (!lastValue!!.equals(ageTxt.text.toString())) {
                ageLb.text = ageTxt.text
                presenter.updateUserData(FirebaseHelper.Companion.UserDataType.AGE, ageTxt.text.toString())
            }
            hideAgeConfirmationButton()
        }
    }

    override fun updatePic(url: String) {
        var callback: com.squareup.picasso.Callback =  object: Callback {
            override fun onSuccess() {
                currentUserManager.setPic((profileImg.drawable as BitmapDrawable).bitmap)
                loadingProfileProgress.visibility = View.INVISIBLE
            }

            override fun onError() { }
        }

        profileImg.loadUrlCircle(url, callback) {
            request -> request.transform(CircleTransform())
        }
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_PHOTO_PICKER && resultCode == Activity.RESULT_OK) {
            loadingProfileProgress.visibility = View.VISIBLE
            presenter.uploadPic(data.data)
        }
    }

    fun showImagePicker(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER)
    }

    fun hideAgeConfirmationButton() {
        ageLb.visibility = View.VISIBLE
        ageEditImg.visibility = View.VISIBLE
        ageTxt.visibility = View.GONE
        ageEditContainer.visibility = View.GONE
    }

    fun hideNameConfirmationButton() {
        nameLb.visibility = View.VISIBLE
        nameEditImg.visibility = View.VISIBLE
        nameTxt.visibility = View.GONE
        nameEditContainer.visibility = View.GONE
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
