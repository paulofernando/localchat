package site.paulo.localchat

import com.squareup.sqlbrite.SqlBrite
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

import rx.observers.TestSubscriber
import site.paulo.localchat.data.local.DatabaseHelper
import site.paulo.localchat.data.local.DbOpenHelper
import site.paulo.localchat.data.model.ribot.Ribot

import junit.framework.Assert.assertEquals
import rx.android.schedulers.AndroidSchedulers
import site.paulo.localchat.data.local.Db
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

    val databaseHelper: DatabaseHelper by lazy {
        val dbHelper = DbOpenHelper(RuntimeEnvironment.application)
        val sqlBrite = SqlBrite.Builder()
                .build()
                .wrapDatabaseHelper(dbHelper, AndroidSchedulers.mainThread())
        DatabaseHelper(sqlBrite)
    }

    @Test
    fun setRibots() {
        val ribots = listOf(TestDataFactory.makeRibot("r1"), TestDataFactory.makeRibot("r2"))

        val result = TestSubscriber<Ribot>()
        databaseHelper.setRibots(ribots).subscribe(result)
        result.assertNoErrors()
        result.assertReceivedOnNext(ribots)

        val cursor = databaseHelper.db.query("SELECT * FROM ${Db.RibotProfileTable.TABLE_NAME}")
        assertEquals(2, cursor.count)

        ribots.forEach {
            cursor.moveToNext()
            assertEquals(it.profile, Db.RibotProfileTable.parseCursor(cursor))
        }

        cursor.close()
    }

    @Test
    fun getRibots() {
        val ribots = listOf(TestDataFactory.makeRibot("r3"), TestDataFactory.makeRibot("r4"))

        databaseHelper.setRibots(ribots).subscribe()

        val result = TestSubscriber<List<Ribot>>()
        databaseHelper.getRibots().subscribe(result)
        result.assertNoErrors()
        result.assertValue(ribots)
    }
}
