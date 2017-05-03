package site.paulo.localchat.ui.signup

import javax.inject.Inject

class SignUpPresenter
@Inject
constructor() : SignUpContract.Presenter() {

    override fun signUp(email: String, password: String, name: String, age: String, gender: String) {
        //TODO implement sign up
        view.showSuccessFullSignUp()
    }

}