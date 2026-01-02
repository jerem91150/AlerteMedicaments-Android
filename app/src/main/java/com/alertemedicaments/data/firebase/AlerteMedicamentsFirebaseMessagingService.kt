package com.alertemedicaments.data.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.alertemedicaments.MainActivity
import com.alertemedicaments.R
import com.alertemedicaments.data.repository.PushTokenRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlerteMedicamentsFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var pushTokenRepository: PushTokenRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch {
            pushTokenRepository.saveAndSyncToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "Alerte Médicaments"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "Nouveau statut de disponibilité"

        val medicationId = remoteMessage.data["medicationId"]
        val status = remoteMessage.data["status"]

        showNotification(title, body, medicationId, status)
    }

    private fun showNotification(
        title: String,
        body: String,
        medicationId: String?,
        status: String?
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medication_alerts"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertes Médicaments",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications de changement de disponibilité des médicaments"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            medicationId?.let { putExtra("medicationId", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val iconColor = when (status) {
            "AVAILABLE" -> android.graphics.Color.parseColor("#22C55E")
            "SHORTAGE" -> android.graphics.Color.parseColor("#F59E0B")
            "UNAVAILABLE" -> android.graphics.Color.parseColor("#EF4444")
            else -> android.graphics.Color.parseColor("#0D9488")
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setColor(iconColor)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        val notificationId = medicationId?.hashCode() ?: System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }
}
