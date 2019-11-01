package de.franky.l.capricornng

import android.content.Context
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.os.Build
import android.widget.Toast


import de.franky.l.capricornng.NG_Utils.NG_Pref


class NG_Wifi_Data {

    internal constructor()
    private var lWifi_Value : Long = 0;
    private var lMobileReceivedBytes : Long = 0;
    private var lMobileSendBytes : Long = 0;
    private var lWLANSendBytes : Long = 0;
    private var lWLANReceivedBytes : Long = 0;
    private val myContext = NG_Application.applicationContext()


    init{    }

    private fun Calc_and_Set_Wifi_Value() {
        val lwlanOffset: Long
        val lNuPi_wlan_RefVal: Long

        try {
            lNuPi_wlan_RefVal = NG_Pref.getLong(R.string.pref_NuPiRef_Wlan_Key, R.string.pref_wlanRefVal_Default)
            lwlanOffset = NG_Pref.getLong(R.string.pref_wlanOffset_Key, 0)
            /*
			 Log.d("JustGetTheValues  iNuPi_wlan_RefVal",String.valueOf(lNuPi_wlan_RefVal));
			 Log.d("JustGetTheValues  lwlanOffset",String.valueOf(lwlanOffset));
			 Log.d("JustGetTheValues  lwlanMessung",String.valueOf(lwlanMessung));
			 Log.d("JustGetTheValues  CurVal.dWlan",String.valueOf(lNuPi_wlan_RefVal +  lwlanMessung - lwlanOffset));
*/
            if (lNuPi_wlan_RefVal != 1L)
            // wenn wlan Numberpickerwert <> 1Byte
            {
                lWifi_Value= lNuPi_wlan_RefVal + Wifi_Total() - lwlanOffset
                if (lWifi_Value < 0)
                // wenn Wert negativ dann in Lollipop
                {
                    lWifi_Value = lWifi_Value  + Mobile_Total()
                }
            } else
            // fuer Wert = 1 Byte (Sonderfall)
            {
                lWifi_Value =  Wifi_Total()
            }
        } catch (e: Exception) {
            Toast.makeText(NG_Application.applicationContext(), "GetValues_Wifi: Something wrong", Toast.LENGTH_SHORT).show()
            lWifi_Value = -1
        }

    }

    fun Wifi_MeasuredValue():Long{
        GetTrafficValues()
        Calc_and_Set_Wifi_Value()
        return lWifi_Value
    }

    fun Wifi_Total():Long{
        GetTrafficValues()
        return lWLANReceivedBytes + lWLANSendBytes
    }

    fun Wifi_Send():Long{
        GetTrafficValues()
        return lWLANSendBytes
    }
    fun Wifi_Received():Long{
        GetTrafficValues()
        return lWLANReceivedBytes
    }
    private fun Mobile_Total():Long{
        GetTrafficValues()
        return lMobileReceivedBytes + lMobileSendBytes
    }

    fun IsWLANandLollipop(): Boolean {
        return IsWifi() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP            // Wenn Lollopop und wlan aktiv dann keine Werte fuer mobile Daten
    }

    private fun GetTrafficValues(){
        lMobileReceivedBytes = TrafficStats.getMobileRxBytes()
        lMobileSendBytes = TrafficStats.getMobileTxBytes()
        lWLANSendBytes = TrafficStats.getTotalTxBytes() - lMobileSendBytes
        lWLANReceivedBytes = TrafficStats.getTotalRxBytes() - lMobileReceivedBytes
    }

    private fun IsWifi(): Boolean {
        return getNetworkType() == ConnectivityManager.TYPE_WIFI     // WiFi eingeschaltet ist
    }
    private fun isNetworkOff(): Boolean {
        return getNetworkType() == -1
    }

    private fun getNetworkType(): Int {
        val cm =
            NG_Application.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager  // Hole Systemdaten um zu erkennen ob
        var iReturn = -1
        val NwI = cm.activeNetworkInfo
        if (NwI != null) {
            iReturn = NwI.type
        }
        return iReturn
    }
}    // End of class NG_Wifi_Data

