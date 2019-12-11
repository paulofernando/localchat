package site.paulo.localchat

import com.google.firebase.auth.FirebaseAuth
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.runners.MockitoJUnitRunner
import rx.Observable
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.User
import site.paulo.localchat.test.common.TestDataFactory
import site.paulo.localchat.ui.dashboard.chat.ChatContract
import site.paulo.localchat.ui.dashboard.chat.ChatPresenter
import site.paulo.localchat.ui.utils.Utils
import site.paulo.localchat.ui.utils.getFirebaseId
import site.paulo.localchat.util.RxSchedulersOverrideRule

@RunWith(MockitoJUnitRunner::class)
class ChatPresenterTest {

    @Rule @JvmField
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @Mock
    lateinit var mockChatMvpView: ChatContract.View
    lateinit var chatPresenter: ChatContract.Presenter

    @Mock
    lateinit var mockDataManager: DataManager
    @Mock
    lateinit var mockFirebaseAuth: FirebaseAuth
    @Mock
    lateinit var mockCurrentUserManager: CurrentUserManager

    @Before
    fun setUp() {
        chatPresenter = ChatPresenter(mockDataManager, mockCurrentUserManager)
        chatPresenter.attachView(mockChatMvpView)
    }

    @After
    fun tearDown() {
        chatPresenter.detachView()
    }

    @Test
    fun loadChatRoomsReturnChatsRoom() {
        val chat = TestDataFactory.makeChat("c1")
        val user = TestDataFactory.makeUser("u1", chat.id)
        val userId = Utils.getFirebaseId(user.email)

        whenever(mockDataManager.getUser(userId)).thenReturn(Observable.just(user))
        whenever(mockDataManager.getChatRoom(chat.id)).thenReturn(Observable.just(chat))

        chatPresenter.loadChatRooms(userId)
        verify(mockChatMvpView).showChat(chat)
        verify(mockChatMvpView, never()).showChatsEmpty()
        verify(mockChatMvpView, never()).showError()
    }

    @Test
    fun loadChatRoomsEmptyList() {
        val user = TestDataFactory.makeUserEmptyChatList("u1")
        val userId = Utils.getFirebaseId(user.email)

        whenever(mockDataManager.getUser(userId)).thenReturn(Observable.just(user))

        chatPresenter.loadChatRooms(userId)
        verify(mockChatMvpView).showChatsEmpty()
        verify(mockChatMvpView, never()).showChat(any())
        verify(mockChatMvpView, never()).showError()
    }

    @Test
    fun loadChatRoomsFails() {
        val user = TestDataFactory.makeUser("u1")
        val userId = Utils.getFirebaseId(user.email)

        whenever(mockDataManager.getUser(userId)).thenReturn(Observable.error<User>(RuntimeException()))

        chatPresenter.loadChatRooms(userId)
        verify(mockChatMvpView).showError()
        verify(mockChatMvpView, never()).showChatsEmpty()
        verify(mockChatMvpView, never()).showChat(any())
    }

}
