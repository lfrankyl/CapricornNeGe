package de.franky.l.capricornng

import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import java.io.IOException
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader


internal object NG_Utils_Std_Values {


    fun dGetAvaiableInternalSDCArd(myContext: Context): Double {
        val NG_StorageDevices =  NG_Utils_CT.getDevices(myContext)                            // Hole alle Geraete aus CT Utils Klasse
        return NG_StorageDevices!![0].freeSpace.toDouble()
    }

    fun dGetTotalInternalSDCArd(myContext: Context): Double {
        val NG_StorageDevices =  NG_Utils_CT.getDevices(myContext)                            // Hole alle Geraete aus CT Utils Klasse
        return NG_StorageDevices!![0].totalSpace.toDouble()
    }

    fun dGetAvaiableExternalSDCArd(myContext: Context): Double {
        val NG_StorageDevices =  NG_Utils_CT.getDevices(myContext)                            // Hole alle Geraete aus CT Utils Klasse
        if (NG_StorageDevices.size > 1)
        return NG_StorageDevices!![1].freeSpace.toDouble()
        else {
            return 0.0
        }
    }

    fun dGetTotalExternalSDCArd(myContext: Context): Double {
        val NG_StorageDevices =  NG_Utils_CT.getDevices(myContext)                            // Hole alle Geraete aus CT Utils Klasse
        if (NG_StorageDevices.size > 1)
            return NG_StorageDevices!![1].totalSpace.toDouble()
        else {
            return 0.0
        }
    }

    fun igetBrightness(context: Context) : Int{
        val iBrightness = Math.round(
            Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                0
            ) / 255.toFloat() * 100
        )
        return iBrightness
    }


    fun igetBatLevel(context: Context) : Int{

        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.applicationContext.registerReceiver(null, ifilter)
        var iBatRawLevel = 0
        var iBatRawScale = -1

        if (batteryStatus != null) {
            iBatRawLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            iBatRawScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        }
        return Math.round(iBatRawLevel / iBatRawScale.toFloat() * 100)

        // Log.d("GetValues_Standard Batterylevel", String.valueOf(iBatteryLevel));

    }
    fun igetBatStatus(context: Context) : Int {

        var iBatteryStatus: Int = 0

        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.applicationContext.registerReceiver(null, ifilter)
        if (batteryStatus != null) {
            iBatteryStatus = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        }
        return  iBatteryStatus

        // Log.d("GetValues_Standard Batterylevel", String.valueOf(iBatteryLevel));

    }

    fun dGetAvaiableRAM(myContext: Context): Double {
        return getMemoryInfo(myContext).availMem.toDouble()
    }

    fun dGetTotalRAM(myContext: Context): Double {
        return getMemoryInfo(myContext).totalMem.toDouble()
    }

    private fun getMemoryInfo(myContext: Context): MemoryInfo {
        val mi = MemoryInfo()
        val activityManager = myContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        return mi
    }

    fun igetCPUload(myContext: Context) : Int{

        var processBuilder: ProcessBuilder
        var Holder = ""
        val DATA = ArrayList<String>()
        DATA.add("/system/bin/cat")
        DATA.add("/proc/cpuinfo")

        val inputStream: InputStream
        val process: Process
        val byteArry: ByteArray
        byteArry = ByteArray(1024)

        try {
            processBuilder = ProcessBuilder(DATA)

            process = processBuilder.start()

            inputStream = process.getInputStream()

            while (inputStream.read(byteArry) !== -1) {

                Holder = Holder + String(byteArry)
            }

            inputStream.close()

        } catch (ex: IOException) {

            ex.printStackTrace()
        }
        Log.d("igetCPUload",Holder)

        return 0
    }


}
