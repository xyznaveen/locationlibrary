package np.com.naveenniraula.locationlibrary.data

data class ReverseGeoCodeModel(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var reverseGeoCode: String = ""
) {

    /**
     *
     * @return [Boolean] true if reverse geocode process has been completed else false.
     */
    fun isReverseGeoCodeComplete(): Boolean {
        return reverseGeoCode.isNotBlank()
    }

}