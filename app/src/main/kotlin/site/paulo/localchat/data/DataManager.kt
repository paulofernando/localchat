package site.paulo.localchat.data

import javax.inject.Inject
import javax.inject.Singleton

import rx.Observable
import site.paulo.localchat.data.local.DatabaseHelper
import site.paulo.localchat.data.model.Ribot
import site.paulo.localchat.data.remote.RibotsService

@Singleton
class DataManager @Inject constructor(private val ribotsService: RibotsService,
                                      private val databaseHelper: DatabaseHelper) {

    fun syncRibots(): Observable<Ribot> {
        return ribotsService.getRibots()
                .concatMap { databaseHelper.setRibots(it) }
    }

    fun getRibots(): Observable<List<Ribot>> {
        return databaseHelper.getRibots().distinct()
    }
}
