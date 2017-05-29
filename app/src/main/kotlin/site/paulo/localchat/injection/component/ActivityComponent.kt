package site.paulo.localchat.injection.component

import dagger.Subcomponent
import site.paulo.localchat.injection.PerActivity
import site.paulo.localchat.injection.module.ActivityModule
import site.paulo.localchat.ui.dashboard.DashboardActivity
import site.paulo.localchat.ui.dashboard.nearby.ChatFragment
import site.paulo.localchat.ui.dashboard.nearby.UsersNearbyFragment
import site.paulo.localchat.ui.room.RoomActivity
import site.paulo.localchat.ui.signin.SignInActivity
import site.paulo.localchat.ui.signup.SignUpActivity

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {
    fun inject(signInActivity: SignInActivity)
    fun inject(signUpActivity: SignUpActivity)
    fun inject(roomActivity: RoomActivity)
    fun inject(dashboardActivity: DashboardActivity)

    fun inject(usersUsersNearbyFragment: UsersNearbyFragment)
    fun inject(chatFragment: ChatFragment)
}
