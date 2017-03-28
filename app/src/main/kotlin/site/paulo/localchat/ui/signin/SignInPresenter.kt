package site.paulo.localchat.ui.signin

import javax.inject.Inject

class SignInPresenter
@Inject
constructor() : SignInContract.Presenter() {

    override fun signIn(email: String) {
        //TODO implement login
        view.showSuccessFullSignIn()
    }

}