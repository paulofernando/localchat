package site.paulo.localchat.injection.module

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.squareup.sqlbrite.BriteDatabase
import com.squareup.sqlbrite.SqlBrite
import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import rx.schedulers.Schedulers
import timber.log.Timber
import site.paulo.localchat.data.local.DbOpenHelper

@Module
class DbModule {

    @Provides
    @Singleton
    fun provideOpenHelper(application: Application): DbOpenHelper {
        return DbOpenHelper(application)
    }

    @Provides
    @Singleton
    fun provideSqlBrite(): SqlBrite {
        return SqlBrite.Builder()
            .logger({ Timber.tag("Database").v(it) })
            .build()
    }

    @Provides
    @Singleton
    fun provideDatabase(sqlBrite: SqlBrite, helper: DbOpenHelper): BriteDatabase {
        val db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io())
        db.setLoggingEnabled(true)
        return db
    }

}
