package np.com.naveenniraula.locationlibrary.internal

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import np.com.naveenniraula.locationlibrary.data.ReverseGeoCodeModel
import np.com.naveenniraula.locationlibrary.util.LogUtil
import np.com.naveenniraula.locationlibrary.util.StringUtil
import java.io.IOException
import java.util.*

class GeoCode(private val context: Context, private var isReverseGeocodeDelayed: Boolean = false) {

    private var reverseGeocodingCompleteListener: ReverseGeocodingCompleteListener? = null

    private var locationType = ""

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

    /**
     * Get location name from latitude and longitude.
     * Before calling this method set a listener: [ReverseGeocodingCompleteListener]
     *
     * @param lat
     * @param lon
     */
    fun resolveFromLatLon(lat: Double, lon: Double) {

        val maxResult = 10

        val hr = HandlerThread("GeoCodeHr")
        hr.start()

        val handler = Handler(hr.looper)
        var runnable: Runnable? = null

        runnable = Runnable {

            if (isReverseGeocodeDelayed) {
                // requesting too much can cause some issues
                // so delay the process by a certain threshold
                Thread.sleep(MIN_DELAY_FOR_REV_GEO_CODE)
            }

            val gc = Geocoder(context, Locale.ENGLISH)
            try {

                val addresses = gc.getFromLocation(lat, lon, maxResult)

                if (addresses.isEmpty()) {
                    notifyNotAvailable(lat, lon)
                    return@Runnable
                }

                val address = getReadableAddressFrom(addresses)

                val model = ReverseGeoCodeModel.new().apply {
                    latitude = lat
                    longitude = lon
                    reverseGeoCode = address
                }
                reverseGeocodingCompleteListener?.onOperationComplete(model)

            } catch (e: IOException) {
                e.printStackTrace()
                notifyNotAvailable(lat, lon)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                notifyNotAvailable(lat, lon)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                notifyNotAvailable(lat, lon)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            // remove callbacks from the handler and also quit the handler thread
            handler.removeCallbacks(runnable)
            val hasHandlerThreadQuit = hr.quit()

            LogUtil.i(
                "GeoCode",
                "we have quit successfully called quit on handler thread -> $hasHandlerThreadQuit"
            )
        }

        handler.post(runnable)
    }

    private fun notifyNotAvailable(lat: Double, lon: Double) {
        reverseGeocodingCompleteListener?.onOperationComplete(ReverseGeoCodeModel.new())
    }

    /**
     * Helper function extracts the address from the first index of the provided list to an
     * acceptable / human readable address.
     * @param addresses the list of resolved address from which we need to extract the address.
     */
    private fun getReadableAddressFrom(addresses: List<Address>): String {
        val address = addresses[0]
        val sb = StringBuilder()
        address.locality?.let { sb.append(it) }
        address.subLocality?.let { sb.append(DELIM).append(it) }
        address.thoroughfare?.let { sb.append(DELIM).append(it) }

        address.getAddressLine(0)?.takeIf { !it.isBlank() }?.let {
            sb.append(it.substringBeforeLast(DELIM))
        }

        LogUtil.v("GeoCode", sb.toString())
        LogUtil.v("GeoCode", address.toString())
        return StringUtil.trimComma(sb.toString())
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

    interface ReverseGeocodingCompleteListener {
        fun onOperationComplete(reverseGeoCodeModel: ReverseGeoCodeModel)
    }

    companion object {
        const val FRESH = "Fresh Location."
        const val STALE = "Stale Location."
        const val OLD_NOT_AVAILABLE = "Unknown."
        const val NOT_AVAILABLE = "location_is_unavailable"
        private const val MIN_DELAY_FOR_REV_GEO_CODE: Long = 250 // in milliseconds
        private const val DELIM: String = ", " // in milliseconds
    }

}
