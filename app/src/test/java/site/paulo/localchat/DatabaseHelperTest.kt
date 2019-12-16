package site.paulo.localchat

import com.squareup.sqlbrite.SqlBrite
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

import rx.observers.TestSubscriber

import junit.framework.Assert.assertEquals
import org.mockito.Mock
import rx.android.schedulers.AndroidSchedulers
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.data.remote.FirebaseHelper
import site.paulo.localchat.test.common.TestDataFactory
import site.paulo.localchat.util.DefaultConfig
import site.paulo.localchat.util.RxSchedulersOverrideRule

/**
 * Unit tests integration with a SQLite Database using Robolectric
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(DefaultConfig.EMULATE_SDK))
class DatabaseHelperTest {

    @Rule @JvmField
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    /*@Mock
    lateinit var mockFirebaseHelper: FirebaseHelper

    @Test
    fun setUser() {
        val user = TestDataFactory.makeUser("u1")

        val result = TestSubscriber<User>()
        mockFirebaseHelper.registerUser(user)
        /*result.assertNoErrors()
        result.assertReceivedOnNext(ribots)*/

    }

    @Test
    fun getRibots() {
        val ribots = listOf(TestDataFactory.makeRibot("r3"), TestDataFactory.makeRibot("r4"))

        databaseHelper.setRibots(ribots).subscribe()

        val result = TestSubscriber<List<Ribot>>()
        databaseHelper.getRibots().subscribe(result)
        result.assertNoErrors()
        result.assertValue(ribots)
    }*/
}
