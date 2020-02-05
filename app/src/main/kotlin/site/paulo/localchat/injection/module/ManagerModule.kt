package site.paulo.localchat.injection.module

import dagger.Module
import dagger.Provides
import site.paulo.localchat.data.MessagesManager
import site.paulo.localchat.data.manager.CurrentUserManager
import javax.inject.Singleton

@Module
class ManagerModule {

    @Provides
    @Singleton
    fun provideCurrentUserManager(): CurrentUserManager {
        return CurrentUserManager.instance
    }

    @Provides
    @Singleton
    fun provideMessagesManager(): MessagesManager {
        return MessagesManager.instance
    }

}