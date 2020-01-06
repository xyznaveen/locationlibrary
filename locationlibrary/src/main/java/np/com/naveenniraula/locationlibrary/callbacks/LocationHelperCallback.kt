package np.com.naveenniraula.locationlibrary.callbacks

import android.location.Location

interface LocationHelperCallback {
    fun onStart()
    fun onStop()
    fun onLocationUpdate(locations: List<Location>)
    fun onInvalidLocation()
    fun onLocationNotAvailable()
}