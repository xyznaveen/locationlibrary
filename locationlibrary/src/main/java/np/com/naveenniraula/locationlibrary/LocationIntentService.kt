package np.com.naveenniraula.locationlibrary

import android.app.IntentService
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import np.com.naveenniraula.locationlibrary.util.LogUtil

class LocationIntentService(name: String = "LocationIntentService") : IntentService(name) {

    override fun onCreate() {
        super.onCreate()
        LogUtil.i("LocationIntentService", "we have installed this.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return Service.START_STICKY
    }

    override fun onHandleIntent(intent: Intent?) {
        LogUtil.d("LocationIntentService", "we have intent $intent")
    }

    companion object {
        fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, LocationIntentService::class.java)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PendingIntent.getForegroundService(
                    context,
                    8789,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                PendingIntent.getService(context, 8789, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }

}