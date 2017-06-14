package site.paulo.localchat

import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import rx.Observable
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.data.remote.FirebaseHelper
import site.paulo.localchat.test.common.TestDataFactory

/**
 * This test class performs local unit tests without dependencies on the Android framework
 * For testing methods in the DataManager follow this approach:
 * 1. Stub mock helper classes that your method relies on. e.g. RetrofitServices or DatabaseHelper
 * 2. Test the Observable using TestSubscriber
 * 3. Optionally write a SEPARATE test that verifies that your method is calling the right helper
 * using Mockito.verify()
 */
@RunWith(MockitoJUnitRunner::class)
class DataManagerTest {

    @Mock
    lateinit var mockFirebaseHelper: FirebaseHelper

    lateinit var dataManager: DataManager


    @Before
    fun setUp() {
        dataManager = DataManager(mockFirebaseHelper)
    }

    @Test
    fun syncUsersEmitsValues() {
        val users = listOf(TestDataFactory.makeUser("u1"), TestDataFactory.makeUser("u2"))
        stubSyncUsersHelperCalls(users)
    }

    private fun stubSyncUsersHelperCalls(users: List<User>) {
        // Stub calls to the ribot service and database helper.
        whenever(mockFirebaseHelper.getUsers()).thenReturn(Observable.just(users))
        /*whenever(mockFirebaseHelper.setRibot(ribots)).thenReturn(Observable.from(ribots))
        whenever(mockFirebaseHelper.setRibot(ribots)).thenReturn(Observable.from(ribots))*/
    }

}
