package de.franky.l.capricornng

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import java.net.NetworkInterface
import java.net.SocketException

import android.content.Context.WIFI_SERVICE
import android.net.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Cpc_frag_netwrk.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Cpc_frag_netwrk.newInstance] factory method to
 * create an instance of this fragment.
 */
class NG_Frag_Netwrk : androidx.fragment.app.Fragment() {

    private var mParam1: String? = null
    private var mParam2: String? = null

    private var Netwrk_View: View? = null
    private var Lout_Container: ViewGroup? = null

    private var mListener: OnFragmentInteractionListener? = null

    val localIpAddress: String?
        get() {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress) {
                            return inetAddress.hostAddress
                        }
                    }
                }
            } catch (ex: SocketException) {
                Log.e("getLocalIpAddress", ex.toString())
            }

            return null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Log.d("Cpc_frag_network","onCreate");
        retainInstance = true
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onStart() {
        super.onStart()
        // Log.d("Cpc_frag_network","onStart");
        FillNetwrkPage()
        NG_Utils.changeTextSize(Lout_Container)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Netwrk_View = inflater.inflate(R.layout.ng_frag_wlan, container, false)
        Lout_Container = Netwrk_View!!.findViewById(R.id.cpc_main)
        return Netwrk_View
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    private fun FillNetwrkPage() {
        val AppContext = NG_Application.applicationContext()

        val mIV = Netwrk_View!!.findViewById<ImageView>(R.id.iV_W_H)
        mIV.setImageResource(R.drawable.ng_lan_frag)
        SetTextViewText(R.id.tV_W_H1, R.string.pref_NetWrk_Caption)

        var sNetWrkType = AppContext.getString(R.string.pref_NetWrk_NoNetWrk)
        var sNetWrkState = AppContext.getString(R.string.pref_NetWrk_NoNetWrkConn)
        var sNetWrkSubType: String
        var iNetIpAdressTitle = R.string.pref_NetWrk_ip6
        var sipAddress: String? = AppContext.getString(R.string.ng_NotKnown)
        var sSSID :String? =  AppContext.getString(R.string.ng_NotKnown)
        var sWifiBSSID : String
        var sWifiSpeed : String
        var sRoaming = AppContext.getString(R.string.ng_pref_Off)
        SetTextViewText(R.id.tV_W_T5, R.string.ng_blankChr)
        SetTextViewText(R.id.tV_W_V5, R.string.ng_blankChr)
        SetTextViewText(R.id.tV_W_H21, R.string.pref_NetWrk_Type)
        SetTextViewText(R.id.tV_W_T1, R.string.pref_NetWrk_Status)
        SetTextViewText(R.id.tV_W_T3, R.string.pref_NetWrk_MobileTyp)
        SetTextViewText(R.id.tV_W_T4, R.string.pref_NetWrk_Rming)
        SetTextViewText(R.id.tV_W_H3, R.string.ng_blankChr)
        SetTextViewText(R.id.tV_W_VH3, R.string.ng_blankChr)

        var TV = Netwrk_View!!.findViewById<TextView>(R.id.tV_W_H21)
        var param = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            2f
        )
        TV.layoutParams = param

        TV = Netwrk_View!!.findViewById(R.id.tV_W_H22)
        param = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            3f
        )
        TV.layoutParams = param

        try {
            val cm =
                AppContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager  // Hole Systemdaten um zu erkennen ob
            val NwI = cm.activeNetworkInfo
            if (NwI != null) {
                sNetWrkSubType = getNetWrkSubTypeName(NwI.subtype)
                sNetWrkType = NwI.typeName
                sNetWrkState = NwI.state.toString()
                if (NwI.isRoaming) {
                    sRoaming = AppContext.getString(R.string.ng_pref_On)
                }
                val wifiMgr = AppContext.getApplicationContext().getSystemService(WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiMgr.connectionInfo //.dhcpInfo//.getConnectionInfo();

                var IsWifi: Boolean
                @SuppressLint("NewApi")
                if(Build.VERSION.SDK_INT >= (Build.VERSION_CODES.O))
                {
                    val Nw = cm.activeNetwork
                    val capabilities = cm.getNetworkCapabilities(Nw);
                    IsWifi= capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) );
                }
                else
                {
                    IsWifi = NwI.type == ConnectivityManager.TYPE_WIFI
                }
                if (IsWifi) {
                    val ip = wifiInfo.ipAddress
                    sipAddress = String.format(
                        "%d.%d.%d.%d",
                        ip and 0xff,
                        ip shr 8 and 0xff,
                        ip shr 16 and 0xff,
                        ip shr 24 and 0xff
                    )
                    sSSID  = findSSIDForWifiInfo(wifiMgr,wifiInfo)

                    sWifiBSSID = wifiMgr.connectionInfo.bssid
                    sWifiSpeed = wifiMgr.connectionInfo.linkSpeed.toString()
                    iNetIpAdressTitle = R.string.pref_NetWrk_ip4
                    SetTextViewText(R.id.tV_W_T3, R.string.pref_NetWrk_SSID)

                    SetTextViewText(R.id.tV_W_H3, R.string.pref_NetWrk_LinkSpeed)
                    SetTextViewText(
                        R.id.tV_W_VH3,
                        sWifiSpeed + AppContext.getString(R.string.ng_blankChr) + WifiInfo.LINK_SPEED_UNITS
                    )
                    SetTextViewText(R.id.tV_W_T4, R.string.pref_NetWrk_BSSID)
                    sRoaming = sWifiBSSID
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        SetTextViewText(R.id.tV_W_T5, R.string.pref_NetWrk_Frq)
                        SetTextViewText(
                            R.id.tV_W_V5, wifiMgr.connectionInfo.frequency.toString() +
                                    AppContext.getString(R.string.ng_blankChr) + WifiInfo.FREQUENCY_UNITS
                        )
                    }

                } else {
                    sSSID = sNetWrkSubType
                    sipAddress = localIpAddress
                    val endIndex = sipAddress!!.indexOf("%",0,false)
                    if ( endIndex >0){
                        sipAddress=sipAddress.substring(0,endIndex)
                    }
                }

            }
        } catch (e: Exception) {
            Log.d("Cpc_frag_netwrk", "Error in NetworkInfo")
        }


        SetTextViewText(R.id.tV_W_H22, sNetWrkType)
        SetTextViewText(R.id.tV_W_V1, sNetWrkState)

        SetTextViewText(R.id.tV_W_T2, iNetIpAdressTitle)
        SetTextViewText(R.id.tV_W_V2, sipAddress)

        SetTextViewText(R.id.tV_W_V3, sSSID)
        SetTextViewText(R.id.tV_W_V4, sRoaming)

        SetTextViewText(R.id.tV_W_T6, R.string.ng_blankChr)
        SetTextViewText(R.id.tV_W_V6, R.string.ng_blankChr)
    }



    fun longToIp(ip: Long): String {
        var myIP = ip
        // Make IPv4 address readable
        val result = StringBuilder(15)

        for (i in 0..3) {
            result.insert(0, java.lang.Long.toString(myIP and 0xff))
            if (i < 3) {
                result.insert(0, '.')
            }
            myIP = myIP shr 8
        }
        return result.toString()
    }

    fun getNetWrkSubTypeName(SubType: Int): String {
        when (SubType) {
            TelephonyManager.NETWORK_TYPE_GPRS -> return "2G GPRS"
            TelephonyManager.NETWORK_TYPE_GSM -> return "2G GSM"
            TelephonyManager.NETWORK_TYPE_EDGE -> return "2G EDGE"
            TelephonyManager.NETWORK_TYPE_CDMA -> return "2G CDMA"
            TelephonyManager.NETWORK_TYPE_1xRTT -> return "2G 1xRTT"
            TelephonyManager.NETWORK_TYPE_IDEN -> return "2G IDEN"
            TelephonyManager.NETWORK_TYPE_UMTS -> return "3G UMTS"
            TelephonyManager.NETWORK_TYPE_EVDO_0 -> return "3G EVDO_0"
            TelephonyManager.NETWORK_TYPE_EVDO_A -> return "3G EVDO_A"
            TelephonyManager.NETWORK_TYPE_HSDPA -> return "3G HSDPA"
            TelephonyManager.NETWORK_TYPE_HSUPA -> return "3G HSUPA"
            TelephonyManager.NETWORK_TYPE_HSPA -> return "3G HSPA"
            TelephonyManager.NETWORK_TYPE_EVDO_B -> return "3G EVDO_B"
            TelephonyManager.NETWORK_TYPE_EHRPD -> return "3G EHRPD"
            TelephonyManager.NETWORK_TYPE_HSPAP -> return "3G HSPAP"
            TelephonyManager.NETWORK_TYPE_TD_SCDMA -> return "3G TD_SCDMA"
            TelephonyManager.NETWORK_TYPE_LTE -> return "4G LTE"
            TelephonyManager.NETWORK_TYPE_IWLAN -> return "IWLAN"
            else -> return "unknown"
        }
    }

    fun findSSIDForWifiInfo(manager: WifiManager, wifiInfo: WifiInfo): String? {

        val listOfConfigurations = manager.configuredNetworks

        for (index in listOfConfigurations.indices) {
            val configuration = listOfConfigurations[index]
            if (configuration.networkId == wifiInfo.networkId) {
                return configuration.SSID
            }
        }

        return null
    }


    internal fun SetTextViewText(iTV: Int, iText: Int) {
        val TV = Netwrk_View!!.findViewById<TextView>(iTV)
        TV.setText(iText)
    }

    internal fun SetTextViewText(iTV: Int, sText: String?) {
        val TV = Netwrk_View!!.findViewById<TextView>(iTV)
        TV.text = sText
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Cpc_frag_mobile.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): NG_Frag_Netwrk {
            val fragment = NG_Frag_Netwrk()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.setArguments(args)
            return fragment
        }
    }


}// Required empty public constructor