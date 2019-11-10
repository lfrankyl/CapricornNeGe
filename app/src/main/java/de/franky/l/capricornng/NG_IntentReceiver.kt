package de.franky.l.capricornng

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.PowerManager
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast



class NG_IntentReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SHUTDOWN) {
            NG_Utils.ActionsWhenShutDown(context)
            Toast.makeText(context, "Capricorn: Device is being shut down", Toast.LENGTH_SHORT).show()
            Log.d("NG_IntentReceiver", "Device is being shut down")
        }
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            NG_Utils.NG_Pref.putLong(context.getString(R.string.pref_MobileMessung_Key), 0)
            NG_Utils.NG_Pref.putLong(context.getString(R.string.pref_wlanOffset_Key), 0)
            Toast.makeText(context, "CapricornNG: Boot completed", Toast.LENGTH_SHORT).show()
        }

        Log.i("NG_IntentReceiver", "OnReceive " + intent.action!!)
    }


}	
