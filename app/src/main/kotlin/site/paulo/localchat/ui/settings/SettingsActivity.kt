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

package site.paulo.localchat.ui.settings

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import org.jetbrains.anko.ctx
import site.paulo.localchat.R
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.settings.profile.ProfileActivity
import site.paulo.localchat.ui.utils.CircleTransform
import site.paulo.localchat.ui.utils.loadUrlAndResizeCircle
import site.paulo.localchat.ui.utils.loadUrlAndResizeCirclePlaceholder
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

    @Inject
    lateinit var currentUserManager: CurrentUserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        presenter.attachView(this)
        setContentView(R.layout.activity_settings)
        ButterKnife.bind(this)

        toolbar.title = resources.getString(R.string.title_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        profileContainer.setOnClickListener {
            launchProfileEditor()
        }

    }

    override fun onResume() {
        super.onResume()
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
        if(currentUserManager.getUser().userPicBitmap != null) {
            profileImage.loadUrlAndResizeCirclePlaceholder(user.pic, ctx.resources.getDimension(R.dimen.image_width_settings).toInt(),
                BitmapDrawable(getResources(), currentUserManager.getUser().userPicBitmap)) {
                request ->
                request.transform(CircleTransform())
            }
        } else {
            profileImage.loadUrlAndResizeCircle(user.pic, ctx.resources.getDimension(R.dimen.image_width_settings).toInt()) {
                request ->
                request.transform(CircleTransform())
            }
        }
        profileName.text = user.name
    }

}
