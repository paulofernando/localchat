package site.paulo.localchat.ui.dashboard.chat

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.BeforeClass
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.test.common.TestDataFactory
import site.paulo.localchat.ui.utils.Utils
import site.paulo.localchat.ui.utils.getFirebaseId

class ChatPresenterTest {

    //Mocks
    private val dataManagerMock: DataManager = mockk()
    private val currentUserManagerMock: CurrentUserManager = mockk()

    @MockK
    lateinit var chatPresenter: ChatContract.Presenter

    @MockK
    lateinit var chatMvpViewMock: ChatContract.View

    @BeforeClass
    internal fun setUpRxSchedulers() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { Schedulers.trampoline() }

    }

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
        chatPresenter = ChatPresenter(dataManagerMock, currentUserManagerMock)
        //chatPresenter.attachView(chatMvpViewMock)
    }

    @AfterEach
    internal fun tearDown() {
        //chatPresenter.detachView()
    }

    @Test
    fun `loadChatRooms - success - return ChatRoom`() {
        val chat = TestDataFactory.makeChat("c1")
        val user = TestDataFactory.makeUser("u1", chat.id)
        val userId = Utils.getFirebaseId(user.email)

        every {dataManagerMock.getUser(userId)} returns Observable.just(user)
        every {dataManagerMock.getChatRoom(chat.id)} returns Observable.just(chat)

        chatPresenter.loadChatRooms(userId)
        confirmVerified(chatPresenter)
    }

}