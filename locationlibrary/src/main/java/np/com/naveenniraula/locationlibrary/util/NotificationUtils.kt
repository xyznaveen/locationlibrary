package np.com.naveenniraula.locationlibrary.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import np.com.naveenniraula.locationlibrary.R

class NotificationUtils(private val context: Context) {

    private lateinit var nub: Builder

    /**
     * Prepare notification based on new values.
     *
     * @return instance of NotificationUtils.Builder.
     */
    private fun getNotificationBuilder(): Builder? {
        if (!::nub.isInitialized) nub = Builder(context)

        nub.isForeground(true)
        nub.withTitle("Notification Title")
        nub.withBody("Notification Body")
        nub.withChannelId("channel-id")
        nub.withChannelName("channel-name")
        nub.withSoundAndVibrationDisabled()
        return nub.build()
    }

    class Builder(context: Context) {
        private val context: Context = context.applicationContext
        private var channelName: String? = null
        private var channelId: String? = null
        private var title: String? = null
        private var body: String? = null
        private var pendingIntent: PendingIntent? = null
        private var isForeground = false
        private var enableSound = false
        private var enableVibration = true
        private var notificationManager: NotificationManager? = null
        private var notificationBuilder: NotificationCompat.Builder? = null
        fun withChannelId(channelId: String?): Builder {
            this.channelId = channelId
            return this
        }

        fun withTitle(title: String?): Builder {
            this.title = title
            return this
        }

        fun withBody(body: String?): Builder {
            this.body = body
            return this
        }

        fun withIntent(clazz: Class<*>?): Builder {
            pendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, clazz),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            return this
        }

        fun hasReceiver(intent: Intent?): Builder {
            pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            return this
        }

        fun withNotificationSound(flag: Boolean): Builder {
            enableSound = flag
            return this
        }

        fun withChannelName(channelName: String?): Builder {
            this.channelName = channelName
            return this
        }

        fun withSoundAndVibrationDisabled(): Builder {
            enableVibration = false
            enableSound = false
            return this
        }

        fun show(id: Int) {
            LogUtil.d(
                "NotificationUtils",
                String.format("body :: %s", body)
            )
            if (notificationManager != null) {
                notificationManager!!.notify(id, notificationBuilder!!.build())
            }
        }

        val notification: Notification
            get() = notificationBuilder!!.build()

        fun isForeground(flag: Boolean) {
            isForeground = flag
        }

        fun build(): Builder {
            val useWhiteIcon =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            notificationBuilder = NotificationCompat.Builder(context, channelId!!)
            notificationBuilder!!
                .setColor(Color.parseColor("#ABCDEF"))
                .setSmallIcon(if (useWhiteIcon) R.drawable.common_google_signin_btn_icon_dark else R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setOngoing(isForeground)
                .setContentIntent(pendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_LOW
                val notificationChannel =
                    NotificationChannel(channelId, channelName, importance)
                notificationBuilder!!.setChannelId(channelId!!)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.parseColor("#ABCDEF")
                if (enableVibration) {
                    notificationChannel.enableVibration(true)
                    notificationChannel.vibrationPattern = longArrayOf(
                        100,
                        200,
                        300,
                        400,
                        500,
                        400,
                        300,
                        200,
                        400
                    )
                } else {
                    notificationChannel.enableVibration(false)
                    notificationChannel.vibrationPattern = longArrayOf(0)
                }
                if (!enableSound) {
                    notificationChannel.setSound(null, null)
                }
                if (notificationManager != null) {
                    notificationManager!!.createNotificationChannel(notificationChannel)
                }
            }
            return this
        }

    }
}