package site.paulo.localchat

import android.app.Application
import android.support.annotation.VisibleForTesting
import site.paulo.localchat.injection.component.ApplicationComponent
import site.paulo.localchat.injection.component.DaggerApplicationComponent
import site.paulo.localchat.injection.module.ApplicationModule
import timber.log.Timber
import com.google.firebase.database.FirebaseDatabase
import com.anupcowkur.reservoir.Reservoir
import java.io.IOException


class BoilerplateApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initDaggerComponent()

        try {
            Reservoir.init(this, 2048) //in bytes
        } catch (e: IOException) {
            Timber.e(e.message)
        }


        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    @VisibleForTesting
    fun initDaggerComponent() {
        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }
}
