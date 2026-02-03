package com.ad.test.core_component

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ad.test.ui.theme.TestTheme
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class PendingIntentAc : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTheme {
                Scaffold {paddingValues ->
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                        val context = LocalContext.current
                        val intent = Intent(context, AlarmReceiver::class.java)
                        val pendingIntent = PendingIntent.getBroadcast(
                            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
                        )
                        var hour by remember { mutableIntStateOf(0) }
                        var minute by remember { mutableIntStateOf(0) }
                        TextField(
                            value = hour.toString(),
                            onValueChange = { hour = it.toIntOrNull() ?: 0 }
                        )
                        TextField(
                            value = minute.toString(),
                            onValueChange = { minute = it.toIntOrNull() ?: 0 }
                        )

                        Button({
                            scheduleAlarm(alarmManager, pendingIntent, hour, minute)
                        }) {
                            Text("Set alarm")
                        }
                    }
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun scheduleAlarm(
        alarmManager: AlarmManager,
        pendingIntent: PendingIntent,
        hour: Int,
        minute: Int
    ) {
        // 1. Kiểm tra quyền cho Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            return
        }

        // 2. Thiết lập thời gian cụ thể trong ngày (ví dụ: 8:30 sáng)
        val triggerTime = LocalTime.of(hour, minute) // 8 giờ, 30 phút

        // 3. Lấy ngày hôm nay và kết hợp với thời gian đã đặt
        var zonedDateTime = ZonedDateTime.now(ZoneId.systemDefault())
            .withHour(triggerTime.hour)
            .withMinute(triggerTime.minute)
            .withSecond(triggerTime.second)
            .withNano(0) // Đặt nano giây về 0 cho chính xác

        // 4. Nếu thời gian đã đặt trong ngày hôm nay đã qua, hãy đặt cho ngày mai
        if (zonedDateTime.isBefore(ZonedDateTime.now())) {
            Log.d("scheduleAlarm", "isBefore")
            zonedDateTime = zonedDateTime.plusDays(1)
        }

        // Lấy thời gian trigger dưới dạng mili giây
        val triggerTimeMillis = zonedDateTime.toInstant().toEpochMilli()

        // 5. Đặt báo thức chính xác
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            pendingIntent
        )
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Thực hiện công việc tại đây (ví dụ: Hiển thị Notification)
        println("Alarm kích hoạt!")
    }
}