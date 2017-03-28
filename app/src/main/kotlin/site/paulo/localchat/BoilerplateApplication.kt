package site.paulo.localchat

import android.app.Application
import android.support.annotation.VisibleForTesting
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber
import site.paulo.localchat.injection.component.ApplicationComponent
import site.paulo.localchat.injection.component.DaggerApplicationComponent
import site.paulo.localchat.injection.module.ApplicationModule
import site.paulo.localchat.BuildConfig

class BoilerplateApplication: Application() {

    lateinit var applicationComponent: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this))
            return
        LeakCanary.install(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initDaggerComponent()
    }

    @VisibleForTesting
    fun initDaggerComponent() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }
}
