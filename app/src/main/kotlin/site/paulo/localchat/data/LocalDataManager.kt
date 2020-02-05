package site.paulo.localchat.data

import com.anupcowkur.reservoir.Reservoir
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataManager
@Inject constructor() {

    fun <T> put(key: String, o: T) {
        Reservoir.put(key, o)
    }

    fun <T> get(key: String, classOfT: Class<T>): T {
        return Reservoir.get<T>(key, classOfT)
    }

    fun contains(key: String): Boolean {
        return Reservoir.contains(key)
    }

}