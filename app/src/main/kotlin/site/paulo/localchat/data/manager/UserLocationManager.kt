/*
 * Copyright 2017 Paulo Fernando
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package site.paulo.localchat.data.manager

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import site.paulo.localchat.data.DataManager
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLocationManager {

    var locationManager: LocationManager? = null
    var dataManager: DataManager? = null
    var context: Context? = null

    private object Holder { val INSTANCE = UserLocationManager() }

    companion object {
        val instance: UserLocationManager by lazy { Holder.INSTANCE }
    }

    fun init(context: Context, dataManager: DataManager) {
        this.context = context
        this.dataManager = dataManager
    }

    fun start() {
        if(context != null && dataManager != null) {
            locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } else {
            Timber.e("Context or DataManager not initiliazed")
        }
    }

    fun stop() {
        locationManager?.removeUpdates(locationListener)
    }

    var locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Timber.d("Location: lon -> ${location.longitude} | lat -> ${location.latitude}")
            dataManager?.updateUserLocation(location)
            Timber.d("Location has been sent to server")
            stop()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

}