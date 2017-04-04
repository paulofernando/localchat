package site.paulo.localchat.injection.module

import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance(FirebaseApp.getInstance())
    }

}