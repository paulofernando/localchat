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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import site.paulo.localchat.data.DataManager
import timber.log.Timber
import javax.inject.Singleton

@Singleton
object UserLocationManager {

    var dataManager: DataManager? = null
    var context: Context? = null
    var callNext: (() -> Unit)? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    fun init(context: Context, dataManager: DataManager) {
        this.context = context
        this.dataManager = dataManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun start(callNext: (() -> Unit)? = null) {
        val ctx = this.context ?: return
        val permission = ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Timber.e("Permission to location denied")
        } else {
            this.callNext = callNext
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        Timber.d("Location: lon -> ${location?.longitude} | lat -> ${location?.latitude}")
                        dataManager?.updateUserLocation(location, UserLocationManager.callNext)
                        Timber.d("Location has been sent to server")
                    }
        }

    }
}