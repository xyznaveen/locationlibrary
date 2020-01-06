package np.com.naveenniraula.locationlibrary.callbacks

interface ReverseGeocodingCompleteCallback {
    fun onGeocodingComplete(address: String, latLon: Pair<Double, Double>)
}