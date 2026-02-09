package com.ad.test.learn

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class YourBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.action?.let {
            when (it) {
                Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                    Log.d(YourBroadcastReceiver::class.simpleName, "Airplane mode changed")
                }

                else -> {
                    Log.d(YourBroadcastReceiver::class.simpleName, "Unknown action: $it")
                }
            }
        }
    }
}