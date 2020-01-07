package np.com.naveenniraula.locationlibrary.callbacks

import np.com.naveenniraula.locationlibrary.data.ReverseGeoCodeModel

interface ReverseGeoCodeCompleteCallback {
    fun onGeoCodeComplete(reverseGeoCodeModel: ReverseGeoCodeModel)
}