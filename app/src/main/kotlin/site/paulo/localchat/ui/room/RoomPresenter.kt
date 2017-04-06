package site.paulo.localchat.ui.signin

import javax.inject.Inject

class RoomPresenter
@Inject
constructor() : RoomContract.Presenter() {

    override fun loadMessages() {
        view.showMessages()
    }

}