package site.paulo.localchat.ui.dashboard.chat

import io.mockk.*
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.manager.CurrentUserManager

class ChatPresenterTest {

    //Mocks
    private val dataManager: DataManager = mockk()
    private val currentUserManager: CurrentUserManager = mockk()

    lateinit var presenter: ChatContract.Presenter

    @Before


    fun loadChatRooms_returnsCHatRoom() {
    }

    @Before
    internal fun setUp() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}