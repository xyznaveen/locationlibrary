package np.com.naveenniraula.locationlibrary.internal

import android.app.Application
import android.app.PendingIntent
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import np.com.naveenniraula.locationlibrary.callbacks.LocationHelperCallback
import np.com.naveenniraula.locationlibrary.data.ReverseGeoCodeModel
import np.com.naveenniraula.locationlibrary.util.LogUtil
import java.util.concurrent.TimeUnit

class LocationHelper(private val context: Application) {

    private lateinit var locationHelperCallback: LocationHelperCallback
    private lateinit var pendingIntent: PendingIntent

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    private val locationRequest by lazy {
        LocationRequest()
            .setInterval(TimeUnit.SECONDS.toMillis(10))
            .setSmallestDisplacement(3.0F)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {

            if (!::locationHelperCallback.isInitialized) {
                Log.e("LocationHelper", "Listener has not been initialized.")
                return
            }

            p0?.let {

                val locations = it.locations

                Log.d("LocationHelper", "we have these many locations ${locations.size}")

                if (locations.isEmpty()) {
                    locationHelperCallback.onInvalidLocation()
                    return@let
                }

                val geoCode = GeoCode(context)
                geoCode.setReverseGeocodingCompleteListener(object :
                    GeoCode.ReverseGeocodingCompleteListener {
                    override fun onOperationComplete(reverseGeoCodeModel: ReverseGeoCodeModel) {
                        Log.d("LocationHelper", "$reverseGeoCodeModel")
                        locationHelperCallback.onLocationUpdate(locations)
                    }
                })
                geoCode.resolveFromLatLon(locations[0].latitude, locations[0].longitude)

                Log.d(
                    "LocationHelper",
                    "we have onLocationResult() -> ${locations[0]}"
                )

            } ?: kotlin.run { locationHelperCallback.onInvalidLocation() }

        }

        override fun onLocationAvailability(p0: LocationAvailability?) {

            if (!::locationHelperCallback.isInitialized) {
                Log.e("LocationHelper", "Listener has not been initialized.")
                return
            }

            if (p0?.isLocationAvailable == false) {
                locationHelperCallback.onLocationNotAvailable()
            }

            Log.d("LocationHelper", "we have onLocationAvailability() -> $p0")
        }
    }

    fun start() {

//        if (!::locationHelperCallback.isInitialized) {
//            Log.e("LocationHelper", "Listener has not been initialized.")
//            return
//        }

        if (::pendingIntent.isInitialized) {
            fusedLocationClient.requestLocationUpdates(locationRequest, pendingIntent)
            LogUtil.i("LocationHelper", "we have ::pendingIntent.isInitialized -> true")
        } else {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        // locationHelperCallback.onStart()
    }

    fun stop() {
        if (::pendingIntent.isInitialized) {
            fusedLocationClient.removeLocationUpdates(pendingIntent)
        } else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        locationHelperCallback.onStop()
    }

    fun setLocationHelperCallback(locationHelperCallback: LocationHelperCallback) {
        this.locationHelperCallback = locationHelperCallback
    }

    fun setPendingIntent(pendingIntent: PendingIntent) {
        this.pendingIntent = pendingIntent
    }

}