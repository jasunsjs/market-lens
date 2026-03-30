package ca.uwaterloo.market_lens.ui.events

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ca.uwaterloo.market_lens.R
import ca.uwaterloo.market_lens.domain.model.MarketEvent
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import ca.uwaterloo.market_lens.app.MainActivity

object NotificationHelper {
    private const val CHANNEL_ID = "market_events_channel"
    private const val CHANNEL_NAME = "Market Events"
    private const val CHANNEL_DESC = "Notifications for simulated market events"
    private const val TAG = "NotificationHelper"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun hasPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun onPermissionGranted() {
        Log.d(TAG, "Notification permission granted")
    }

    fun showEventNotification(context: Context, event: MarketEvent) {
        if (!hasPermission(context)) {
            Log.w(TAG, "Cannot show notification: Permission not granted")
            return
        }

        // Intent is used to nav alert to event page
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("eventId", event.id)
        }

        // Attach a pending intent to the notification for navigation
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 
            event.id.hashCode(), 
            intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("ALERT: ${event.tickerKey} moved ${event.percentMove}%.")
            .setContentText(event.briefDescription)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(event.id.hashCode(), builder.build())
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException while showing notification", e)
        }
    }
}
