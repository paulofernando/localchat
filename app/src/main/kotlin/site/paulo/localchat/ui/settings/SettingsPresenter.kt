package site.paulo.localchat.ui.settings

import site.paulo.localchat.data.manager.CurrentUserManager
import javax.inject.Inject

class SettingsPresenter
@Inject
constructor(private val currentUserManager: CurrentUserManager) : SettingsContract.Presenter() {

    override fun loadCurrentUser() {
        view.showCurrentUserData(currentUserManager.getUser())
    }

}