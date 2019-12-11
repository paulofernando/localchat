package site.paulo.localchat

import com.google.firebase.auth.FirebaseAuth
import com.nhaarman.mockito_kotlin.whenever

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyListOf
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.runners.MockitoJUnitRunner
import rx.Observable
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.test.common.TestDataFactory
import site.paulo.localchat.ui.dashboard.nearby.UsersNearbyContract
import site.paulo.localchat.ui.dashboard.nearby.UserNearbyPresenter
import site.paulo.localchat.util.RxSchedulersOverrideRule

@RunWith(MockitoJUnitRunner::class)
class UserNearbyPresenterTest {

    @Rule @JvmField
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @Mock
    lateinit var mockUsersNearbyMvpView: UsersNearbyContract.View
    lateinit var usersNearbyPresenter: UsersNearbyContract.Presenter

    @Mock
    lateinit var mockDataManager: DataManager
    @Mock
    lateinit var mockFirebaseAuth: FirebaseAuth
    @Mock
    lateinit var mockCurrentUser: CurrentUserManager

    @Before
    fun setUp() {
        usersNearbyPresenter = UserNearbyPresenter(mockDataManager, mockFirebaseAuth, mockCurrentUser)
        usersNearbyPresenter.attachView(mockUsersNearbyMvpView)
    }

    @After
    fun tearDown() {
        usersNearbyPresenter.detachView()
    }

    @Test
    fun loadNearbyUsersReturnNearbyUsers() {
        val users = TestDataFactory.makeListUsers(10)
        whenever(mockDataManager.getUsers()).thenReturn(Observable.just(users))

        usersNearbyPresenter.loadNearbyUsers()
        verify(mockUsersNearbyMvpView).showNearbyUsers(users)
        verify(mockUsersNearbyMvpView, never()).showNearbyUsersEmpty()
        verify(mockUsersNearbyMvpView, never()).showError()
    }

    @Test
    fun loadNearbyUsersReturnsEmptyList() {
        whenever(mockDataManager.getUsers()).thenReturn(Observable.just(emptyList<User>()))

        usersNearbyPresenter.loadNearbyUsers()
        verify(mockUsersNearbyMvpView).showNearbyUsersEmpty()
        verify(mockUsersNearbyMvpView, never()).showNearbyUsers(anyListOf(User::class.java))
        verify(mockUsersNearbyMvpView, never()).showError()
    }

    @Test
    fun loadNearbyUsersFails() {
        whenever(mockDataManager.getUsers()).thenReturn(Observable.error<List<User>>(RuntimeException()))

        usersNearbyPresenter.loadNearbyUsers()
        verify(mockUsersNearbyMvpView).showError()
        verify(mockUsersNearbyMvpView, never()).showNearbyUsersEmpty()
        verify(mockUsersNearbyMvpView, never()).showNearbyUsers(anyListOf(User::class.java))
    }

}
