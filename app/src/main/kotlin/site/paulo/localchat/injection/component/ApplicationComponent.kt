package site.paulo.localchat.injection.component

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Component
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.injection.ApplicationContext
import site.paulo.localchat.injection.module.ApplicationModule
import site.paulo.localchat.injection.module.FirebaseModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, FirebaseModule::class))
interface ApplicationComponent {

    @ApplicationContext fun context(): Context
    fun application(): Application
    fun dataManager(): DataManager
    fun firebaseDatabase(): FirebaseDatabase
    fun firebaseAuth(): FirebaseAuth
}
