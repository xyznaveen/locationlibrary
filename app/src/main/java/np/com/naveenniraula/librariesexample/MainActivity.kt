package np.com.naveenniraula.librariesexample

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import np.com.naveenniraula.locationlibrary.LocationLibrary
import np.com.naveenniraula.locationlibrary.callbacks.LocationHelperCallback
import np.com.naveenniraula.locationlibrary.callbacks.ReverseGeoCodeCompleteCallback
import np.com.naveenniraula.locationlibrary.data.ReverseGeoCodeModel
import np.com.naveenniraula.locationlibrary.util.LocationUtil
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        checkAndRequestPermission()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

    }

    @AfterPermissionGranted(LOCATION_PERM_REQ)
    private fun checkAndRequestPermission() {

        val locationPerms = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (EasyPermissions.hasPermissions(this, *locationPerms)) {
            doWhatIsRequired()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Compulsory.",
                LOCATION_PERM_REQ,
                * locationPerms
            )
        }

    }

    private fun doWhatIsRequired() {

        LocationLibrary.Builder(application)
            .withLocationHelperCallback(object : LocationHelperCallback {
                override fun onStart() {
                    Timber.d("location request has started.")
                }

                override fun onStop() {
                    Timber.d("location request has been stopped.")
                }

                override fun onLocationUpdate(locations: List<Location>) {

                    val loc = locations[0]

                    Timber.d("we have $loc")

                }

                override fun onInvalidLocation() {
                    Timber.d("location is invalid.")
                }

                override fun onLocationNotAvailable() {
                    Timber.d("location is not available.")
                }

            }).build().start()

        val reverseGeocodingCompleteCallback = object : ReverseGeoCodeCompleteCallback {
            override fun onGeoCodeComplete(reverseGeoCodeModel: ReverseGeoCodeModel) {
                Timber.d("we have successfully completed this. $reverseGeoCodeModel")
            }
        }

        val rgc = LocationLibrary.ReverseGeocoder(application)
        LocationUtil.getLocations(10, 20_00_000).forEach {
            rgc.add(it.latitude, it.longitude, reverseGeocodingCompleteCallback)
        }
        rgc.work()

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        checkAndRequestPermission()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        checkAndRequestPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val LOCATION_PERM_REQ = 111
    }

}
