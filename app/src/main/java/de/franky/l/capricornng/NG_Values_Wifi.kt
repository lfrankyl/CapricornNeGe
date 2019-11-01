package de.franky.l.capricornng

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.TrafficStats
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import android.widget.Toast


import de.franky.l.capricornng.NG_Utils.NG_Pref


class NG_Values_Wifi {

    internal constructor()
    private var lWifi_Value : Long = 0;
    private var lMobileReceivedBytes : Long = 0;
    private var lMobileSendBytes : Long = 0;
    private var lWLANSendBytes : Long = 0;
    private var lWLANReceivedBytes : Long = 0;
    private var bWIFISetup: Boolean =     false                               // Flag ob Mobile Referenzwert wurde im WLAN eingegeben, dann stimmt der Messwert nämlich nicht
    private var lNuPiWifiRevVal : Long   = 0                                  // Wert des Numberpicker für den Startwert der verbrauchten Wifi Daten
    private var lWifiOffset : Long   = 0                                      // Offsetwert Wifi Daten
    private var sWifiStartDate : String =""                                   // Start-Datum der Wifi-Messung
    private val myContext = NG_Application.applicationContext()


    init{    }

    internal fun bWIFISetup(bValue: Boolean)                { NG_Pref.putBool(myContext.getString(R.string.pref_WifiSetup_Key), bValue)    }
    internal fun bWIFISetup(): Boolean                      { return NG_Pref.getBool(R.string.pref_WifiSetup_Key, false)      }

    internal fun lNuPiWifiRevVal(lValue: Long)              { NG_Utils.NG_Pref.putLong(myContext.getString(R.string.pref_NuPiRef_Wlan_Key), lValue) }
    internal fun lNuPiWifiRevVal(): Long                    { return NG_Utils.NG_Pref.getLong(R.string.pref_NuPiRef_Wlan_Key, R.string.pref_wlanRefVal_Default) }

    internal fun lWifiOffset(lValue: Long)                  { NG_Utils.NG_Pref.putLong(myContext.getString(R.string.pref_wlanOffset_Key), lValue) }
    internal fun lWifiOffset(): Long                        { return NG_Utils.NG_Pref.getLong(R.string.pref_wlanOffset_Key, R.string.pref_wlanRefVal_Default) }

    internal fun sWifiStartDate(sValue: String)             { NG_Utils.NG_Pref.putString(myContext.getString(R.string.pref_wlanStartDate_Key), sValue) }
    internal fun sWifiStartDate(): String                   { return NG_Utils.NG_Pref.getString(myContext.getString(R.string.pref_wlanStartDate_Key),myContext.getString(R.string.DefaultText)) }

    private fun Calc_Wifi_Value() {
        val lwlanOffset: Long
        val lNuPi_wlan_RefVal: Long

        try {
            lNuPi_wlan_RefVal = NG_Utils.NG_Wifi_Values.lNuPiWifiRevVal()
            lwlanOffset = NG_Utils.NG_Wifi_Values.lWifiOffset()
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
        Calc_Wifi_Value()
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
    fun Mobile_Total():Long{
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

        var bIsWifi = false
        try {
            val cm = myContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager  // Hole Systemdaten um zu erkennen ob
            val NwI = cm.activeNetworkInfo
            if (NwI != null) {
                val wifiMgr = myContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiMgr.connectionInfo //.dhcpInfo//.getConnectionInfo();

                @SuppressLint("NewApi")
                if(Build.VERSION.SDK_INT >= (Build.VERSION_CODES.O))
                {
                    val Nw = cm.activeNetwork
                    val capabilities = cm.getNetworkCapabilities(Nw);
                    bIsWifi =  capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) );
                }
                else
                {
                    bIsWifi=  getNetworkType() == ConnectivityManager.TYPE_WIFI     // WiFi eingeschaltet ist
                }


            }
        } catch (e: Exception) {
            Log.d("NG_Values_Wifi", "Error in NetworkInfo")
        }
        return bIsWifi
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
}    // End of class NG_Values_Wifi

