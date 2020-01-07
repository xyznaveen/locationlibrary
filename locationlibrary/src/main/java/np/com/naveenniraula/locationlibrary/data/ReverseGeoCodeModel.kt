package np.com.naveenniraula.locationlibrary.data

data class ReverseGeoCodeModel(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var reverseGeoCode: String = INVALID_LOCATION
) {

    /**
     *
     * @return [Boolean] true if reverse geocode process has been completed else false.
     */
    fun isReverseGeoCodeComplete(): Boolean {
        return reverseGeoCode.isNotBlank()
    }

    fun isLocationInvalid(): Boolean {
        return reverseGeoCode == INVALID_LOCATION
    }

    companion object {
        const val INVALID_LOCATION = "location_not_available"

        fun new(): ReverseGeoCodeModel {
            return ReverseGeoCodeModel()
        }
    }

}