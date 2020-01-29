package site.paulo.localchat.ui.room

import com.google.firebase.storage.FirebaseStorage
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
import site.paulo.localchat.test.common.TestDataFactory
import java.lang.RuntimeException

class RoomPresenterTest {
    lateinit var roomPresenter: RoomContract.Presenter

    //Mocks
    private val dataManagerMock: DataManager = mockk()
    private val currentUserManagerMock: CurrentUserManager = mockk()
    private val firebaseStorageMock: FirebaseStorage = mockk()
    @RelaxedMockK
    lateinit var roomMvpViewMock: RoomContract.View

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
        MockKAnnotations.init(this)

        roomPresenter = RoomPresenter(dataManagerMock, currentUserManagerMock, firebaseStorageMock)
        roomPresenter.attachView(roomMvpViewMock)

        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @AfterEach
    internal fun tearDown() {
        roomPresenter.detachView()
    }

    @Test
    fun `getChatData - success - chat with data`() {
        val chat = TestDataFactory.makeChat("c1")
        val user = TestDataFactory.makeUser("u1", chat.id)

        every {dataManagerMock.getChatRoom(chat.id)} returns Maybe.just(chat)

        roomPresenter.getChatData(chat.id)

        verify {
            roomMvpViewMock.showChat(chat)
        }
        verify(exactly = 0) {
            roomMvpViewMock.showEmptyChatRoom()
            roomMvpViewMock.showError()
        }
    }

    @Test
    fun `getChatData - success - empty chat`() {
        var chat = TestDataFactory.makeEmptyChat()
        val user = TestDataFactory.makeUser("u1", chat.id)

        every {dataManagerMock.getChatRoom(chat.id)} returns Maybe.just(chat)

        roomPresenter.getChatData(chat.id)

        verify {
            roomMvpViewMock.showEmptyChatRoom()
        }
        verify(exactly = 0) {
            roomMvpViewMock.showChat(chat)
            roomMvpViewMock.showError()
        }
    }

    @Test
    fun `getChatData - failure`() {
        val chat = TestDataFactory.makeChat("c1")
        val user = TestDataFactory.makeUser("u1", chat.id)

        every {dataManagerMock.getChatRoom(chat.id)} returns Maybe.error(RuntimeException())

        roomPresenter.getChatData(chat.id)

        verify {
            roomMvpViewMock.showError()
        }
        verify(exactly = 0) {
            roomMvpViewMock.showChat(chat)
            roomMvpViewMock.showEmptyChatRoom()
        }
    }

    @Test
    fun `sendMessage - empty message`() {
        val chatMessage = TestDataFactory.makeEmptyChatMessage()

        roomPresenter.sendMessage(chatMessage, "id")

        verify(exactly = 0) {
            dataManagerMock.sendMessage(chatMessage, "id", any())
        }
    }
}