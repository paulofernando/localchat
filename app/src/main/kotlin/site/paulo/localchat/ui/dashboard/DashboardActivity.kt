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

package site.paulo.localchat.ui.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.widget.Toolbar
import android.view.*
import org.jetbrains.anko.startActivity
import site.paulo.localchat.R
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.manager.UserLocationManager
import site.paulo.localchat.ui.about.AboutActivity
import site.paulo.localchat.ui.base.BaseActivity
import site.paulo.localchat.ui.dashboard.nearby.ChatFragment
import site.paulo.localchat.ui.dashboard.nearby.UsersNearbyFragment
import site.paulo.localchat.ui.settings.SettingsActivity
import javax.inject.Inject

import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity: BaseActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * [FragmentPagerAdapter] derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    /**
     * The [ViewPager] that will host the section contents.
     */
    private var mViewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private val tabIcons = intArrayOf(R.drawable.nearby, R.drawable.chat)

    private val usersNearbyFragment: UsersNearbyFragment = UsersNearbyFragment()
    private val chatFragment: ChatFragment = ChatFragment()

    @Inject
    lateinit var presenter: DashboardPresenter

    @Inject
    lateinit var dataManager: DataManager

    companion object {
        const val REQUEST_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivity()

        val params = toolbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
        setSupportActionBar(toolbar)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        tabs.setupWithViewPager(container)
        //setupTabIcons()

        startUserLocationManager()
    }

    fun setupActivity() {
        setContentView(R.layout.activity_dashboard)
        activityComponent.inject(this)
    }

    private fun setupTabIcons() {
        tabLayout?.getTabAt(0)?.setIcon(tabIcons[0])
        tabLayout?.getTabAt(1)?.setIcon(tabIcons[1])
    }

    private fun startUserLocationManager() {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        when (permissionCheck) {
            PackageManager.PERMISSION_GRANTED -> {
                var userLocationManager: UserLocationManager = UserLocationManager.instance
                userLocationManager.init(this, dataManager)
            }
            PackageManager.PERMISSION_DENIED -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION ->  {
                var userLocationManager: UserLocationManager = UserLocationManager.instance
                userLocationManager.init(this, dataManager)
                usersNearbyFragment.presenter.loadUsers()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        when(item.itemId) {
            R.id.action_settings -> {
                startActivity<SettingsActivity>()
                return true
            }
            R.id.action_signout -> {
                presenter.logout()
                finish()
            }
            R.id.action_about -> {
                startActivity<AboutActivity>()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_dashboard, container, false)
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private const val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return if (position == 0) usersNearbyFragment
                   else chatFragment
        }

        override fun getCount(): Int {
            // Show 2 total pages.
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return resources.getString(R.string.tab_title_1)
                1 -> return resources.getString(R.string.tab_title_2)
            }
            return null
        }
    }
}
