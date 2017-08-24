package site.paulo.localchat

import android.app.Application
import android.support.annotation.VisibleForTesting
import site.paulo.localchat.injection.component.ApplicationComponent
import site.paulo.localchat.injection.component.DaggerApplicationComponent
import site.paulo.localchat.injection.module.ApplicationModule
import timber.log.Timber
import com.google.firebase.database.FirebaseDatabase


class BoilerplateApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initDaggerComponent()

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    @VisibleForTesting
    fun initDaggerComponent() {
        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }
}
