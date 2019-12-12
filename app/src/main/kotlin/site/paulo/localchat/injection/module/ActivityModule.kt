package site.paulo.localchat.injection.module

import androidx.fragment.app.FragmentActivity
import dagger.Module
import dagger.Provides
import site.paulo.localchat.injection.ActivityContext
import site.paulo.localchat.injection.PerActivity

@Module
class ActivityModule(private val activity: FragmentActivity?) {

    @Provides
    @PerActivity
    internal fun provideActivity(): FragmentActivity? {
        return activity
    }

    @Provides
    @PerActivity
    @ActivityContext
    internal fun providesContext(): FragmentActivity? {
        return activity
    }
}
