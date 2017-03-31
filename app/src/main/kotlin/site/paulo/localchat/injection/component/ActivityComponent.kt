package site.paulo.localchat.injection.component

import dagger.Subcomponent
import site.paulo.localchat.injection.PerActivity
import site.paulo.localchat.injection.module.ActivityModule
import site.paulo.localchat.ui.user.UserActivity
import site.paulo.localchat.ui.main.MainActivity
import site.paulo.localchat.ui.signin.SignInActivity

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(signInActivity: SignInActivity)
    fun inject(userActivity: UserActivity)
}
