package site.paulo.localchat.ui.base

import android.support.v4.app.Fragment
import android.os.Bundle
import site.paulo.localchat.BoilerplateApplication
import site.paulo.localchat.injection.component.ActivityComponent
import site.paulo.localchat.injection.component.ConfigPersistentComponent
import site.paulo.localchat.injection.component.DaggerConfigPersistentComponent
import site.paulo.localchat.injection.module.ActivityModule
import timber.log.Timber
import java.util.*
import java.util.concurrent.atomic.AtomicLong

open class BaseFragment : Fragment() {

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
        activityId = savedInstanceState?.getLong(BaseFragment.Companion.KEY_ACTIVITY_ID) ?: BaseFragment.Companion.NEXT_ID.getAndIncrement()

        if (BaseFragment.Companion.componentsMap[activityId] != null)
            Timber.i("Reusing ConfigPersistentComponent id=%d", activityId)

        val configPersistentComponent = BaseFragment.Companion.componentsMap.getOrPut(activityId, {
            Timber.i("Creating new ConfigPersistentComponent id=%d", activityId)

            val component = (activity.application.applicationContext as BoilerplateApplication).applicationComponent

            DaggerConfigPersistentComponent.builder()
                .applicationComponent(component)
                .build()
        })

        activityComponent = configPersistentComponent.activityComponent(ActivityModule(activity))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(BaseFragment.Companion.KEY_ACTIVITY_ID, activityId)
    }

    override fun onDestroy() {
        if (!activity.isChangingConfigurations) {
            Timber.i("Clearing ConfigPersistentComponent id=%d", activityId)
            BaseFragment.Companion.componentsMap.remove(activityId)
        }
        super.onDestroy()
    }
}
