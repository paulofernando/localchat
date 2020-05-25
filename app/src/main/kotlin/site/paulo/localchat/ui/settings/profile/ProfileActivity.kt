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
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.squareup.picasso.Callback
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_settings.*
import site.paulo.localchat.R
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.data.remote.FirebaseHelper
import site.paulo.localchat.exception.MissingCurrentUserException
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.settings.ProfilePresenter
import site.paulo.localchat.ui.utils.CircleTransform
import site.paulo.localchat.ui.utils.loadUrlCircle
import javax.inject.Inject


class ProfileActivity : BaseActivity(), ProfileContract.View {

    internal val RC_PHOTO_PICKER = 1

    lateinit var user: User
    lateinit var lastLocation: String

    var lastValue: String? = null

    @Inject
    lateinit var presenter: ProfilePresenter

    @Inject
    lateinit var currentUserManager: CurrentUserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivity()

        user = intent.getParcelableExtra<User>(resources.getString(R.string.user_name))
        var profilePic: Bitmap = intent.getParcelableExtra<Bitmap>(resources.getString(R.string.user_profile_image))

        profileProfileImg.setImageBitmap(profilePic)
        showCurrentUserData()
    }

    private fun setupActivity() {
        activityComponent.inject(this)
        presenter.attachView(this)
        setContentView(R.layout.activity_profile)

        toolbarProfile.title = resources.getString(R.string.title_profile)
        setSupportActionBar(toolbarProfile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun showCurrentUserData() {
        nameUserProfileLabel.text = user.name
        nameUserProfileTxt.setText(user.name, TextView.BufferType.EDITABLE)
        ageUserProfileLabel.text = user.age.toString()
        ageUserProfileTxt.setText(user.age.toString(), TextView.BufferType.EDITABLE)
        emailUserProfileLabel.text = user.email

        when (user.gender) {
            "m" -> genderUserProfileLabel.text = resources.getString(R.string.lb_gender_male)
            "f" -> genderUserProfileLabel.text = resources.getString(R.string.lb_gender_female)
        }

    }

    /**************** Name *********************/

    override fun editName(view: View) {
        cancelAgeEdition(ageUserProfileTxt)
        nameUserProfileLabel.visibility = View.GONE
        nameUserEditProfileImg.visibility = View.GONE
        nameUserProfileTxt.visibility = View.VISIBLE
        nameUserEditProfileContainer.visibility = View.VISIBLE
        lastValue = nameUserProfileLabel.text.toString()
    }

    override fun cancelNameEdition(view: View) {
        hideNameConfirmationButton()
    }

    override fun confirmNameEdition(view: View) {
        val name = nameUserProfileTxt.text.toString()
        val minimumCharsName: Int = resources.getString(R.string.number_minimum_chars_user_name).toInt()
        val maximumCharsName: Int = resources.getString(R.string.number_maximum_chars_user_name).toInt()
        if (name.isEmpty() || name.length < minimumCharsName || name.length > maximumCharsName) {
            nameUserProfileTxt.error = resources.getString(R.string.error_chars_bet_numbers, minimumCharsName, maximumCharsName)
        } else {
            nameUserProfileTxt.error = null
            val value = lastValue ?: return
            if (value != nameUserProfileTxt.text.toString()) {
                nameUserProfileLabel.text = nameUserProfileTxt.text
                presenter.updateUserData(FirebaseHelper.Companion.UserDataType.NAME, nameUserProfileTxt.text.toString())
            }
            hideNameConfirmationButton()
        }
    }

    /**************** Age *********************/

    override fun editAge(view: View) {
        cancelAgeEdition(nameUserProfileTxt)
        ageUserProfileLabel.visibility = View.GONE
        ageUserEditProfileImg.visibility = View.GONE
        ageUserProfileTxt.visibility = View.VISIBLE
        ageUserEditProfileContainer.visibility = View.VISIBLE
        lastValue = nameUserProfileLabel.text.toString()
    }

    override fun cancelAgeEdition(view: View) {
        hideAgeConfirmationButton()
    }

    override fun confirmAgeEdition(view: View) {
        val age = ageUserProfileTxt.text.toString().toLong()
        val minimumAge: Int = resources.getString(R.string.number_minimum_age).toInt()
        val maximumAge: Int = resources.getString(R.string.number_maximum_age).toInt()
        if (ageUserProfileTxt.text.isEmpty() || age !in (minimumAge - 1)..(maximumAge + 1)) {
            ageUserProfileTxt.error = resources.getString(R.string.error_numbers_bet, minimumAge, maximumAge)
        } else {
            ageUserProfileTxt.error = null
            val value = lastValue ?: return
            if (value != ageUserProfileTxt.text.toString()) {
                ageUserProfileLabel.text = ageUserProfileTxt.text
                presenter.updateUserData(FirebaseHelper.Companion.UserDataType.AGE, ageUserProfileTxt.text.toString())
            }
            hideAgeConfirmationButton()
        }
    }

    override fun updatePic(url: String) {
        var callback: com.squareup.picasso.Callback =  object: Callback {
            override fun onSuccess() {
                val currentUser = currentUserManager.getUser() ?: return
                currentUser.userPicBitmap = (profileProfileImg.drawable as BitmapDrawable).bitmap
                loadingProfileProgress.visibility = View.INVISIBLE
            }

            override fun onError(e: Exception) { }
        }

        profileProfileImg.loadUrlCircle(url, callback) {
            request -> request.transform(CircleTransform())
        }
    }

    override fun onStart() {
        super.onStart()
        editProfileImageBtn.postDelayed({ animateButton() }, 300)
    }

    override fun onBackPressed() {
        animateReverseButton()
        editProfileImageBtn.postDelayed({ super.onBackPressed() }, 200)
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_PHOTO_PICKER && resultCode == Activity.RESULT_OK) {
            loadingProfileProgress.visibility = View.VISIBLE
            val picUri = data?.data ?: return
            presenter.uploadPic(picUri)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun showImagePicker(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER)
    }

    private fun hideAgeConfirmationButton() {
        ageUserProfileLabel.visibility = View.VISIBLE
        ageUserEditProfileImg.visibility = View.VISIBLE
        ageUserProfileTxt.visibility = View.GONE
        ageUserEditProfileContainer.visibility = View.GONE
    }

    private fun hideNameConfirmationButton() {
        nameUserProfileLabel.visibility = View.VISIBLE
        nameUserEditProfileImg.visibility = View.VISIBLE
        nameUserProfileTxt.visibility = View.GONE
        nameUserEditProfileContainer.visibility = View.GONE
    }

    private fun animateButton() {
        editProfileImageBtn.visibility = View.VISIBLE
        val myAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.profile_button_scale)
        editProfileImageBtn.startAnimation(myAnim)
    }

    private fun animateReverseButton() {
        val myAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.profile_button_scale_reverse)
        myAnim.setAnimationListener(object : AnimationListener {
            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                editProfileImageBtn.visibility = View.INVISIBLE
            }
        })
        editProfileImageBtn.startAnimation(myAnim)
    }


}
