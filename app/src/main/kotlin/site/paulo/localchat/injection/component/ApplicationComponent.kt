package site.paulo.localchat.injection.component

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Component
import site.paulo.localchat.data.DataManager
import site.paulo.localchat.data.LocalDataManager
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.remote.FirebaseHelper
import site.paulo.localchat.injection.ApplicationContext
import site.paulo.localchat.injection.module.ApplicationModule
import site.paulo.localchat.injection.module.FirebaseModule
import site.paulo.localchat.injection.module.ManagerModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, FirebaseModule::class, ManagerModule::class))
interface ApplicationComponent {

    @ApplicationContext fun context(): Context
    fun application(): Application
    fun dataManager(): DataManager
    fun localDataManager(): LocalDataManager
    fun currentUserManager(): CurrentUserManager
    fun firebaseDatabase(): FirebaseDatabase
    fun firebaseHelper(): FirebaseHelper
    fun firebaseAuth(): FirebaseAuth
    fun firebaseStorage(): FirebaseStorage
}
