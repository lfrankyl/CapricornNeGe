package de.franky.l.capricornng

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView

object NG_Utils {

    var NG_Pref          = NG_SharePref()
    var NG_Val_Wifi      = NG_Values_Wifi()
    var NG_Val_Time_Cots = NG_Values_Time_Constraints()
    var NG_Val_Mob_Data  = NG_Values_Mobile_Data()
    var NG_Val_Calls     = NG_Values_Calls()

    lateinit var NG_Receiver: NG_IntentReceiver

    var bIsReceiverRegistered: Boolean =    false                                // Flag um festzustellen ob der Broadcastreceiver "WidgetIntentreceiver" bereits registriert ist

    fun RegisterIntentReceiver() {
        if (!bIsReceiverRegistered) {
            val iFilter = IntentFilter()
            iFilter.addAction(Intent.ACTION_USER_PRESENT)
            iFilter.addAction(Intent.ACTION_SHUTDOWN)
            // iFilter.addAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            NG_Receiver = NG_IntentReceiver()
            NG_Application.applicationContext().registerReceiver(NG_Receiver, iFilter)
            Log.d("NG_IntentReceiver", "RegisterIntentReceiver")
            bIsReceiverRegistered = true
            //Intent intent = new Intent(sIntentAction);
            //intent.setClass(Cpc_Application.getContext(), Cpc_WidgetIntentReceiver.class);
        }
    }

    fun UnRegisterIntentReceiver() {
        if (bIsReceiverRegistered) {
            Log.d("NG_IntentReceiver", "UnRegisterIntentReceiver")
            NG_Application.applicationContext().unregisterReceiver(NG_Receiver)
            bIsReceiverRegistered = false
        }
    }


    fun ActionsWhenShutDown(context: Context) {

//        var lNumberPicker = Cpc_Utils.CurVal.DispData[5].getNumber() as Long
//        NG_Pref.putLong(context.getString(R.string.pref_MobileMessung_Key), 0)
//        NG_Pref.putLong(context.getString(R.string.pref_NuPiRef_Mob_Key), lNumberPicker)
//        NG_Pref.putLong(Cpc_Std_Data.pref_MobileMessungSaved_Key, 0)
//
//        // Log.d("ShutDown Logging pref_NuPiRef_Mob_Key",String.valueOf(lNumberPicker));
//
//        lNumberPicker = Cpc_Utils.CurVal.DispData[6].getNumber()
//        NG_Pref.putLong(context.getString(R.string.pref_wlanOffset_Key), 0)
//        NG_Pref.putLong(context.getString(R.string.pref_NuPiRef_Wlan_Key), lNumberPicker)
    }


    fun MakeOutString(NumberToBeCalc: Double): String {
        var myNumberToBeCalc = NumberToBeCalc
        // Makes a string based on the data val in bytes. Takes care does not longer than 4 digits
        // and deletes point if its the last digit
        // Input are Bytes, method calcs best fit to value Kbyte, MByte, GByte, TByte
        var sTmpResult = "n/a"

        // Log.d("MakeOutString vor",String.valueOf(NumberToBeCalc));
        if (myNumberToBeCalc >= 0) {
            // Log.d("MakeOutString nach",String.valueOf(NumberToBeCalc));
            if (myNumberToBeCalc >= (1024 * 1024 * 1024).toDouble() * 1024)
                myNumberToBeCalc = myNumberToBeCalc / 1024.0 / 1024.0 / 1024.0 / 1024.0
            else if (myNumberToBeCalc >= 1024 * 1024 * 1024)
                myNumberToBeCalc = myNumberToBeCalc / 1024.0 / 1024.0 / 1024.0
            else if (myNumberToBeCalc >= 1024 * 1024)
                myNumberToBeCalc = myNumberToBeCalc / 1024.0 / 1024.0
            else if (myNumberToBeCalc >= 1024) myNumberToBeCalc = myNumberToBeCalc / 1024

            sTmpResult = myNumberToBeCalc.toString()

            if (sTmpResult.length > 4) {
                sTmpResult = sTmpResult.substring(0, 4)            // Result is max. 4 chr incl. decimal point
            }
            if (sTmpResult.substring(sTmpResult.length - 1, sTmpResult.length).equals(
                    ".",
                    ignoreCase = true
                )
            ) {  // decimal point noe is the last char
                sTmpResult =
                    sTmpResult.substring(0, sTmpResult.length - 1)                                // then delete
            }
        }
        return sTmpResult

    }

    fun CalcUnit(NumberToBeCalc: Double): String {
        // Determines the unit of the Data val shown in the widget and the App.
        // Input are Byte, Method calcs the best fit to unit KByte, MByte, GByte, TByte
        val sSizeUnit: String

        if (NumberToBeCalc < 0)
            sSizeUnit = ""
        else if (NumberToBeCalc >= (1024 * 1024 * 1024).toDouble() * 1024)
            sSizeUnit = " TB"
        else if (NumberToBeCalc >= 1024 * 1024 * 1024)
            sSizeUnit = " GB"
        else if (NumberToBeCalc >= 1024 * 1024)
            sSizeUnit = " MB"
        else if (NumberToBeCalc >= 1024)
            sSizeUnit = " KB"
        else
            sSizeUnit = " Byte"

        return sSizeUnit

    }

    fun iCalcDataVal(lValueAsBytes: Long): Int {
        // Calculates the number which is shown in the numberpickers
        // Must be used together with iCalcDataUnitIndex.
        var fDataValNew = lValueAsBytes.toFloat()

        while (fDataValNew > 9999) {
            fDataValNew = fDataValNew / 1024
        }
        return Math.round(fDataValNew)
    }

    fun lCalcDataValToByte(lValue: Long, iUnit: Int): Long {
        // Calculates the data value into bytes based on the Unit Index
        // Works at any time
        var lValueByte = lValue
        if (iUnit == 1)
            lValueByte = lValueByte * 1024
        else if (iUnit == 2)
            lValueByte = lValueByte * 1024 * 1024
        else if (iUnit == 3)
            lValueByte = lValueByte * 1024 * 1024 * 1024
        else if (iUnit == 4) lValueByte = lValueByte * 1024 * 1024 * 1024 * 1024

        return lValueByte
    }

    fun iCalcDataUnitIndex(lValueAsBytes: Long): Int {
        // Calculates the Index of the unit 0=Byte, 1=KByte, 2=MByte, 3=GByte, 4=TByte
        // based on the number as bytes, which is used in the numberpickers
        // Must be used together which iCalcDataVal

        var fDataValNew = lValueAsBytes.toFloat()
        var iUnitIndex = 0

        while (fDataValNew > 9999) {
            fDataValNew = fDataValNew / 1024
            iUnitIndex = iUnitIndex + 1
        }
        return iUnitIndex
    }

    fun changeTextSize(root: ViewGroup?) {
        val iTextSize =
            NG_Pref.getString(R.string.pref_TextSizeApp_Key, R.string.pref_TextSize_Default)?.toFloat()
        if (root != null) {
            for (i in 0 until root.childCount) {
                val v = root.getChildAt(i)
                if (v is TextView) {
                    v.textSize = iTextSize!!
                } else if (v is ViewGroup) {
                    changeTextSize(v)
                }
            }
        }
    }

}