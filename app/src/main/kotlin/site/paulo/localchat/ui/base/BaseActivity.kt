package site.paulo.localchat.ui.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import timber.log.Timber
import site.paulo.localchat.BoilerplateApplication
import site.paulo.localchat.injection.component.ActivityComponent
import site.paulo.localchat.injection.component.ConfigPersistentComponent
import site.paulo.localchat.injection.component.DaggerConfigPersistentComponent
import site.paulo.localchat.injection.module.ActivityModule
import java.util.HashMap
import java.util.concurrent.atomic.AtomicLong

open class BaseActivity: AppCompatActivity() {

    companion object {
        @JvmStatic private val KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID"
        @JvmStatic private val NEXT_ID = AtomicLong(0)
        @JvmStatic private val componentsMap = HashMap<Long, ConfigPersistentComponent>()
    }

    private var activityId: Long = 0
    lateinit var activityComponent: ActivityComponent
        get

    @Suppress("UsePropertyAccessSyntax")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create the ActivityComponent and reuses cached ConfigPersistentComponent if this is
        // being called after a configuration change.
        activityId = savedInstanceState?.getLong(BaseActivity.Companion.KEY_ACTIVITY_ID) ?: BaseActivity.Companion.NEXT_ID.getAndIncrement()

        if (BaseActivity.Companion.componentsMap[activityId] != null)
            Timber.i("Reusing ConfigPersistentComponent id=%d", activityId)

        val configPersistentComponent = BaseActivity.Companion.componentsMap.getOrPut(activityId, {
            Timber.i("Creating new ConfigPersistentComponent id=%d", activityId)

            val component = (applicationContext as BoilerplateApplication).applicationComponent

            DaggerConfigPersistentComponent.builder()
                    .applicationComponent(component)
                    .build()
        })

        activityComponent = configPersistentComponent.activityComponent(ActivityModule(this))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(BaseActivity.Companion.KEY_ACTIVITY_ID, activityId)
    }

    override fun onDestroy() {
        if (!isChangingConfigurations) {
            Timber.i("Clearing ConfigPersistentComponent id=%d", activityId)
            BaseActivity.Companion.componentsMap.remove(activityId)
        }
        super.onDestroy()
    }
}
