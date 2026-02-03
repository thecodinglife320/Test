package com.ad.test.core_component

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import com.ad.test.R
import com.ad.test.ui.theme.TestTheme

// Create the NotificationChannel, but only on API 26+ because
// the NotificationChannel class is not in the support library
const val CHANNEL_ID = "deliveryStatus"

class PostingNotifications : ComponentActivity() {

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Delivery status"
            val descriptionText = "Your delivery status"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        setContent {
            TestTheme {
                Scaffold {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        val context = LocalContext.current
                        LaunchedEffect(Unit) {
                            val notificationBuilder =
                                NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.outline_save_24)
                                    .setContentTitle("Delivery")
                                    .setContentText("Food is ready!")
                                    .setStyle(NotificationCompat
                                        .BigTextStyle()
                                        .bigText("Some long text message")
                                    )
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                        }
                    }
                }
            }
        }
    }

}