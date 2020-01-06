package np.com.naveenniraula.locationlibrary.internal

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

class GeoCode(private val context: Context) {

    init {
        Log.d("GeoCode", "location init geo.")
    }

    private var reverseGeocodingCompleteListener: ReverseGeocodingCompleteListener? = null

    private var locationType = ""

    /**
     * Get location name from latitude and longitude.
     * Before calling this method set a listener: [ReverseGeocodingCompleteListener]
     *
     * @param lat
     * @param lon
     */
    fun resolveFromLatLon(lat: Double, lon: Double) {
        // FromLatLonToAddress(this).execute(lat, lon)

        val maxResult = 1

        val hr = HandlerThread("GeoCodeHr")
        hr.start()

        val handler = Handler(hr.looper)
        var runnable: Runnable? = null

        runnable = Runnable {

            Thread.sleep(2500)

            val gc = Geocoder(context, Locale.ENGLISH)
            try {

                val addresses = gc.getFromLocation(lat, lon, maxResult)

                if (addresses.isEmpty()) {
                    reverseGeocodingCompleteListener?.onAddressResolved(
                        NOT_AVAILABLE,
                        lat,
                        lon
                    )
                    return@Runnable
                }

                val address = getReadableAddressFrom(addresses)

                reverseGeocodingCompleteListener?.onAddressResolved(address, lat, lon)

            } catch (e: IOException) {
                e.printStackTrace()
                reverseGeocodingCompleteListener?.onAddressResolved(
                    NOT_AVAILABLE,
                    lat,
                    lon
                )
            } catch (e: InterruptedException) {
                e.printStackTrace()
                reverseGeocodingCompleteListener?.onAddressResolved(
                    NOT_AVAILABLE,
                    lat,
                    lon
                )
            }

            // stop this
            val hasHandlerThreadQuit = hr.quit()
            handler.removeCallbacks(runnable)

            Log.d("GeoCode", "we have quit successfully $hasHandlerThreadQuit")
        }

        handler.post(runnable)
    }

    private fun getReadableAddressFrom(addresses: List<Address>): String {
        val address = addresses[0]
        val sb = StringBuilder()
        val delim = ", "

        if (isNotNull(address.locality)) {

            sb.append(address.locality)
        }

        if (isNotNull(address.subLocality)) {

            sb.append(delim)
            sb.append(address.subLocality)
        }

        if (isNotNull(address.thoroughfare)) {

            sb.append(delim)
            sb.append(address.thoroughfare)
        }

        val tempAddressLine = address.getAddressLine(0)
        if (sb.toString().isBlank() && isNotNull(tempAddressLine)) {
            sb.append(tempAddressLine.substringBeforeLast(','))
        }

        return sb.toString()
    }

    private fun isNotNull(str: String?): Boolean {
        return str != null
    }

    /**
     * Get location name from latitude and longitude.
     * Before calling this method set a listener: [ReverseGeocodingCompleteListener]
     *
     * @param lat
     * @param lon
     */
    fun resolveFromLatLon(lat: String, lon: String) {
        Log.d("GeoCode", "trying to resolve $lat $lon")
        resolveFromLatLon(lat.toDouble(), lon.toDouble())
    }

    fun setReverseGeocodingCompleteListener(reverseGeocodingCompleteListener: ReverseGeocodingCompleteListener) {
        this.reverseGeocodingCompleteListener = reverseGeocodingCompleteListener
    }

    fun setLocationType(locationType: String) {
        this.locationType = locationType
    }

    fun getLocationType(): String {
        return locationType
    }

    internal class FromLatLonToAddress(geoCode: GeoCode) :
        AsyncTask<Double, Void, List<Address>>() {

        private val weakReference: WeakReference<GeoCode> = WeakReference(geoCode)
        private var lat: Double = 0.0
        private var lon: Double = 0.0

        override fun doInBackground(vararg position: Double?): List<Address>? {

            val ref = weakReference.get() ?: return emptyList()

            if (position.size == 2) {
                val gc = Geocoder(ref.context, Locale.ENGLISH)
                try {

                    lat = position[0]!!
                    lon = position[1]!!

                    val addresses = gc.getFromLocation(lat, lon, 1)
                    Thread.sleep(500)
                    return addresses
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            return null
        }

        override fun onPostExecute(addresses: List<Address>?) {
            super.onPostExecute(addresses)
            if (addresses == null || addresses.isEmpty()) {
                weakReference.get()?.reverseGeocodingCompleteListener?.onAddressResolved(
                    NOT_AVAILABLE,
                    lat,
                    lon
                )
                return
            }

            val address = addresses[0]
            val sb = StringBuilder()
            val delim = ", "

            if (isNotNull(address.locality)) {

                sb.append(address.locality)
            }

            if (isNotNull(address.subLocality)) {

                sb.append(delim)
                sb.append(address.subLocality)
            }

            if (isNotNull(address.thoroughfare)) {

                sb.append(delim)
                sb.append(address.thoroughfare)
            }

            val tempAddressLine = address.getAddressLine(0)
            if (sb.toString().isBlank() && isNotNull(tempAddressLine)) {
                sb.append(tempAddressLine.substringBeforeLast(','))
            }

            if (weakReference.get() != null && weakReference.get()?.reverseGeocodingCompleteListener != null) {
                weakReference.get()?.reverseGeocodingCompleteListener?.onAddressResolved(
                    sb.toString(),
                    lat = lat,
                    lon = lon
                )
            }

        }

        private fun isNotNull(str: String?): Boolean {
            return str != null
        }
    }

    interface ReverseGeocodingCompleteListener {
        fun onAddressResolved(address: String, lat: Double, lon: Double)
    }

    companion object {
        const val FRESH = "Fresh Location."
        const val STALE = "Stale Location."
        const val NOT_AVAILABLE = "Unknown."
    }

}
