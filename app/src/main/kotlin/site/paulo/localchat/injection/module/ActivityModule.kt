package site.paulo.localchat.injection.module

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides
import site.paulo.localchat.injection.ActivityContext
import site.paulo.localchat.injection.PerActivity

@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    @PerActivity
    internal fun provideActivity(): Activity {
        return activity
    }

    @Provides
    @PerActivity
    @ActivityContext
    internal fun providesContext(): Context {
        return activity
    }
}
