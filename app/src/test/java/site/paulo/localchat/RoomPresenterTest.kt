package site.paulo.localchat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
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
import site.paulo.localchat.data.model.firebase.Chat
import site.paulo.localchat.data.model.firebase.ChatMessage
import site.paulo.localchat.test.common.TestDataFactory
import site.paulo.localchat.ui.room.RoomContract
import site.paulo.localchat.ui.room.RoomPresenter
import site.paulo.localchat.util.RxSchedulersOverrideRule

@RunWith(MockitoJUnitRunner::class)
class RoomPresenterTest {

    @Rule @JvmField
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @Mock
    lateinit var mockChatMvpView: RoomContract.View
    lateinit var roomPresenter: RoomContract.Presenter

    @Mock
    lateinit var mockDataManager: DataManager
    @Mock
    lateinit var mockFirebaseStorage: FirebaseStorage
    @Mock
    lateinit var mockCurrentUserManager: CurrentUserManager

    @Before
    fun setUp() {
        roomPresenter = RoomPresenter(mockDataManager, mockCurrentUserManager, mockFirebaseStorage)
        roomPresenter.attachView(mockChatMvpView)
    }

    @After
    fun tearDown() {
        roomPresenter.detachView()
    }

    @Test
    fun getChatDataAndShowThem() {
        val chat = TestDataFactory.makeChat("c1")

        whenever(mockDataManager.getChatRoom(chat.id)).thenReturn(Observable.just(chat))

        roomPresenter.getChatData(chat.id)
        verify(mockChatMvpView).showChat(chat)
        verify(mockChatMvpView, never()).showEmptyChatRoom()
        verify(mockChatMvpView, never()).showError()
    }

    @Test
    fun getChatDataReturnsEmptyChat() {
        var chat = Chat(id = "")

        whenever(mockDataManager.getChatRoom(chat.id)).thenReturn(Observable.just(chat))

        roomPresenter.getChatData(chat.id)
        verify(mockChatMvpView, never()).showChat(chat)
        verify(mockChatMvpView).showEmptyChatRoom()
        verify(mockChatMvpView, never()).showError()
    }

    @Test
    fun getChatDataFails() {
        val chat = TestDataFactory.makeChat("c1")

        whenever(mockDataManager.getChatRoom(chat.id)).thenReturn(Observable.error<Chat>(RuntimeException()))

        roomPresenter.getChatData(chat.id)
        verify(mockChatMvpView, never()).showChat(chat)
        verify(mockChatMvpView, never()).showEmptyChatRoom()
        verify(mockChatMvpView).showError()
    }
}
