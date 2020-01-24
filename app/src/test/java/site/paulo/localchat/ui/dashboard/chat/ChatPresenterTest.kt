package site.paulo.localchat.ui.dashboard.chat

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Maybe
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
import java.lang.RuntimeException

class ChatPresenterTest {

    lateinit var chatPresenter: ChatContract.Presenter

    //Mocks
    private val dataManagerMock: DataManager = mockk()
    private val currentUserManagerMock: CurrentUserManager = mockk()
    @RelaxedMockK lateinit var chatMvpViewMock: ChatContract.View

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
        MockKAnnotations.init(this)

        chatPresenter = ChatPresenter(dataManagerMock, currentUserManagerMock)
        chatPresenter.attachView(chatMvpViewMock)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @AfterEach
    internal fun tearDown() {
        chatPresenter.detachView()
    }

    @Test
    fun `loadChatRooms - success - return ChatRoom`() {
        val chat = TestDataFactory.makeChat("c1")
        val user = TestDataFactory.makeUser("u1", chat.id)
        val userId = Utils.getFirebaseId(user.email)

        every {dataManagerMock.getUser(userId)} returns Maybe.just(user)
        every {dataManagerMock.getChatRoom(chat.id)} returns Maybe.just(chat)

        chatPresenter.loadChatRooms(userId)

        verify(exactly = 0) {
            chatMvpViewMock.showChatsEmpty()
            chatMvpViewMock.showError()
        }
    }

    @Test
    fun `loadChatRooms - success - show empty chat`() {
        val user = TestDataFactory.makeUserEmptyChatList("u1")
        val userId = Utils.getFirebaseId(user.email)

        every {dataManagerMock.getUser(userId)} returns Maybe.just(user)

        chatPresenter.loadChatRooms(userId)

        verify(exactly = 1) {
            chatMvpViewMock.showChatsEmpty()
        }
        verify(exactly = 0) {
            chatMvpViewMock.showError()
        }

    }

    @Test
    fun `loadChatRooms - failure`() {
        val user = TestDataFactory.makeUser("u1")
        val userId = Utils.getFirebaseId(user.email)

        every {dataManagerMock.getUser(userId)} returns Maybe.error(RuntimeException())

        chatPresenter.loadChatRooms(userId)

        verify(exactly = 1) {
            chatMvpViewMock.showError()
        }
        verify(exactly = 0) {
            chatMvpViewMock.showChatsEmpty()
        }

    }

}