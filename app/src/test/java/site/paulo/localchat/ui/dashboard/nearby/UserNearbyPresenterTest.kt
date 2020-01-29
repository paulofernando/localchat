package site.paulo.localchat.ui.dashboard.nearby

import com.google.firebase.auth.FirebaseAuth
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Maybe
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.test.common.TestDataFactory
import site.paulo.localchat.ui.dashboard.TestUtils
import java.lang.RuntimeException


class UserNearbyPresenterTest {
    lateinit var usersNearbyPresenter: UsersNearbyContract.Presenter

    //Mocks
    private val dataManagerMock: DataManager = mockk()
    private val firebaseAuthMock: FirebaseAuth = mockk()

    @RelaxedMockK
    lateinit var currentUserManagerMock: CurrentUserManager
    @RelaxedMockK
    lateinit var usersNearby: UsersNearbyContract.View

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
        MockKAnnotations.init(this)

        usersNearbyPresenter = UserNearbyPresenter(dataManagerMock, firebaseAuthMock, currentUserManagerMock)
        usersNearbyPresenter.attachView(usersNearby)

        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @AfterEach
    internal fun tearDown() {
        usersNearbyPresenter.detachView()
    }

    @Test
    fun `loadUsers - success - register current user`() {
        val user = TestDataFactory.makeUser("u1", "chatId", "test@test.com")
        val user2 = TestDataFactory.makeUser("u2", "chatId")
        val listOfUsers = arrayListOf<User>()
        listOfUsers.add(user)
        listOfUsers.add(user2)

        every {dataManagerMock.getUsers()} returns Maybe.just(listOfUsers)
        every {firebaseAuthMock.currentUser} returns TestUtils.getMockFirebaseUser()

        usersNearbyPresenter.loadUsers()

        verify {
            currentUserManagerMock.setUser(user)
        }
        verify(exactly = 0) {
            usersNearby.showNearbyUsersEmpty()
            usersNearby.showError()
        }
    }

    @Test
    fun `loadUsers - success - register current user not being first in the list`() {
        val user = TestDataFactory.makeUser("u1", "chatId", "test@test.com")
        val user2 = TestDataFactory.makeUser("u2", "chatId")
        val listOfUsers = arrayListOf<User>()
        listOfUsers.add(user2)
        listOfUsers.add(user)

        every {dataManagerMock.getUsers()} returns Maybe.just(listOfUsers)
        every {firebaseAuthMock.currentUser} returns TestUtils.getMockFirebaseUser()

        usersNearbyPresenter.loadUsers()

        verify {
            currentUserManagerMock.setUser(user)
        }
        verify(exactly = 0) {
            usersNearby.showNearbyUsersEmpty()
            usersNearby.showError()
        }
    }

    @Test
    fun `loadUsers - failure - show empty list`() {
        every {dataManagerMock.getUsers()} returns Maybe.just(emptyList())
        every {firebaseAuthMock.currentUser} returns TestUtils.getMockFirebaseUser()

        usersNearbyPresenter.loadUsers()

        verify {
            usersNearby.showNearbyUsersEmpty()
        }
        verify(exactly = 0) {
            currentUserManagerMock.setUser(any())
            usersNearby.showError()
        }
    }

    @Test
    fun `loadUsers - failure - show error`() {
        every {dataManagerMock.getUsers()} returns Maybe.error(RuntimeException())

        usersNearbyPresenter.loadUsers()

        verify {
            usersNearby.showError()
        }
        verify(exactly = 0) {
            currentUserManagerMock.setUser(any())
            usersNearby.showNearbyUsersEmpty()
        }
    }


}


