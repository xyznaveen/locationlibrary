package np.com.naveenniraula.locationlibrary

import android.app.Application
import android.app.PendingIntent
import android.location.Location
import android.util.Log
import np.com.naveenniraula.locationlibrary.callbacks.LocationHelperCallback
import np.com.naveenniraula.locationlibrary.callbacks.ReverseGeoCodeCompleteCallback
import np.com.naveenniraula.locationlibrary.data.ReverseGeoCodeModel
import np.com.naveenniraula.locationlibrary.internal.GeoCode
import np.com.naveenniraula.locationlibrary.internal.LocationHelper

class LocationLibrary {

    private constructor()

    class Builder(private val application: Application) {

        private lateinit var locationHelperCallback: LocationHelperCallback
        private lateinit var pendingIntent: PendingIntent

        fun withLocationHelperCallback(locationHelperCallback: LocationHelperCallback): Builder {
            this.locationHelperCallback = locationHelperCallback
            return this
        }

        fun withPendingIntent(): Builder {
            val pendingIntent = LocationIntentService.getPendingIntent(application)
            this.pendingIntent = pendingIntent
            return this
        }

        fun build(): LocationHelper {
            return LocationHelper(application).apply {
                if (::pendingIntent.isInitialized) {
                    setPendingIntent(pendingIntent)
                } else {
                    setLocationHelperCallback(locationHelperCallback)
                }
            }
        }

    }

    class ReverseGeocoder(private val application: Application) {

        private var isOngoing = false
        private var currentIndex = 0

        private val toBeReverseGeocoded =
            arrayListOf<Pair<Pair<Double, Double>, ReverseGeoCodeCompleteCallback>>()

        private val gc = GeoCode(application)
        private val reverseGeocodingCompleteListener: GeoCode.ReverseGeocodingCompleteListener =
            object : GeoCode.ReverseGeocodingCompleteListener {

                override fun onOperationComplete(reverseGeoCodeModel: ReverseGeoCodeModel) {
                    val currentPair = toBeReverseGeocoded[currentIndex]
                    currentPair.second.onGeoCodeComplete(reverseGeoCodeModel)

                    Log.d(
                        "ReverseGeocoder",
                        "we have result for $currentIndex $reverseGeoCodeModel"
                    )

                    ++currentIndex
                    doResolve()
                }
            }

        fun add(pair: Pair<Pair<Double, Double>, ReverseGeoCodeCompleteCallback>): ReverseGeocoder {
            toBeReverseGeocoded.add(pair)
            return this
        }

        fun add(
            lat: Double,
            lon: Double,
            callback: ReverseGeoCodeCompleteCallback
        ): ReverseGeocoder {
            val pair = Pair(Pair(lat, lon), callback)
            toBeReverseGeocoded.add(pair)
            return this
        }

        fun add(
            location: Location,
            callback: ReverseGeoCodeCompleteCallback
        ): ReverseGeocoder {
            val pair = Pair(Pair(location.latitude, location.longitude), callback)
            toBeReverseGeocoded.add(pair)
            return this
        }

        fun resolve(pair: Pair<Pair<Double, Double>, ReverseGeoCodeCompleteCallback>) {

            val toResolve = pair.first
            val callback = pair.second

        }

        fun work() {

            if (isOngoing) {
                Log.d("ReverseGeocoder", "already in progress.")
            }

            doResolve()

        }

        private fun doResolve() {

            if (currentIndex >= toBeReverseGeocoded.size) {
                reset()
                return
            }

            val currentPair = toBeReverseGeocoded[currentIndex].first

            val gc = GeoCode(application)
            gc.setReverseGeocodingCompleteListener(reverseGeocodingCompleteListener)
            gc.resolveFromLatLon(currentPair.first, currentPair.second)

        }

        private fun reset() {
            isOngoing = false
            currentIndex = 0
        }

    }

}