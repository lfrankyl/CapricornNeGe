package de.franky.l.capricornng

import android.net.TrafficStats

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.preference.PreferenceFragmentCompat

import java.util.GregorianCalendar
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import de.franky.l.capricornng.NG_Utils.NG_Val_Wifi


class NG_Settings_Frag_Wlan : PreferenceFragmentCompat() {

    var myPrefWlanData:Preference? = null
    var myPrefWlanDate:EditTextPreference? = null
    var myPrefWlanResetButton:Preference? = null
    private var Wlan_View: View? = null
    private var Lout_Container: ViewGroup? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Lout_Container = view as ViewGroup
    }

    override fun onStart() {
        super.onStart()
        NG_Utils.changeTextSize(Lout_Container)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?,rootKey: String?) {
        //super.onCreate(savedInstanceState)

        // Load the preferences from an XML resource
        // Log.d("Cpc_SettingsFragment","On create");
        setPreferencesFromResource(R.xml.ng_setting_frag_wlan,rootKey)

        myPrefWlanDate = findPreference(getString(R.string.pref_wlanStartDate_Key))
        myPrefWlanData = findPreference(getString(R.string.pref_NuPiRef_Wlan_Key))
        val lCurrentSummText = NG_Val_Wifi.lNuPiWifiRevVal()
        var sCurrentSummText = NG_Utils.MakeOutString(lCurrentSummText.toDouble())
        sCurrentSummText = sCurrentSummText + " " + NG_Utils.CalcUnit(lCurrentSummText.toDouble())
        val sDate = NG_Val_Wifi.sWifiStartDate()
        myPrefWlanDate?.setSummary(sDate)
        myPrefWlanData?.setSummary(sCurrentSummText)

        myPrefWlanResetButton = findPreference(getString(R.string.pref_wlanResetOffset_Key))
        myPrefWlanResetButton!!.setOnPreferenceClickListener(Preference.OnPreferenceClickListener {
                //code for what you want it to do
                SetAfterPositivDialog(0)
                true
            }
          )
        }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        val clearRecentDialog = preference as? NG_Dlg_NuPi_Preference
        if (clearRecentDialog != null) {
            val dialogFragment = NG_Dlg_NuPi_Dialog.newInstance(clearRecentDialog.key)
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(fragmentManager!!, null)
            dialogFragment.positiveResult=
                {
                    val lwlanValueByte = NG_Utils.lCalcDataValToByte(dialogFragment.PickerData!!.getValue().toLong(),dialogFragment.PickerUnit!!.getValue())
                    SetAfterPositivDialog(lwlanValueByte)
                }
            dialogFragment.CreateDialogView=
                {
                    val sUnitValues: Array<String>
                    sUnitValues = NG_Application.applicationContext().resources.getStringArray(R.array.pref_DataUnits)

                    val iWlanDataMax = 9999
                    val iWlanDataMin = 0

                    val iWlanUnitMax = sUnitValues.size - 1
                    val iWlanUnitMin = 0

                    // Log.d("Cpc_Dlg_NuPi_MobData_RevVal","onCreateDialogView");


                    val mTV = it.findViewById(R.id.tv_nupi_refval_explanation) as TextView;
                    mTV.setText(NG_Application.applicationContext().getResources().getString(R.string.pref_wlanRefVal_Explanation))

                    dialogFragment.PickerData = it.findViewById(R.id.nupi_refval_value)

                    // Initialize state
                    dialogFragment.PickerData!!.maxValue = iWlanDataMax
                    dialogFragment.PickerData!!.minValue = iWlanDataMin

                    // Log.d("onCreateDialogView",String.valueOf(iwlanDataDefVal));

                    //Log.d("onCreateDialogView",String.valueOf(iWlanPickerValueNeu));
                    dialogFragment.PickerData!!.wrapSelectorWheel = true
                    dialogFragment.PickerData!!.value = NG_Utils.iCalcDataVal(NG_Utils.NG_Val_Wifi.lNuPiWifiRevVal())
                    dialogFragment.PickerUnit = it.findViewById(R.id.nupi_refval_unit)

                    // Initialize state
                    dialogFragment.PickerUnit!!.maxValue = iWlanUnitMax
                    dialogFragment.PickerUnit!!.minValue = iWlanUnitMin
                    dialogFragment.PickerUnit!!.wrapSelectorWheel = true  // Works only on SKD >=16,
                    dialogFragment.PickerUnit!!.displayedValues = sUnitValues
                    dialogFragment.PickerUnit!!.value = NG_Utils.iCalcDataUnitIndex(NG_Utils.NG_Val_Wifi.lNuPiWifiRevVal())
                }
            }
        else
        {
            super.onDisplayPreferenceDialog(preference)
        }

    }
    private fun SetAfterPositivDialog(lValNumPicker:Long){
        var sNewSummText: String =""
        val lMessung = TrafficStats.getTotalTxBytes() + TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes() - TrafficStats.getMobileTxBytes()
        val myCal = GregorianCalendar()
        val sDate = android.text.format.DateFormat.format("dd.MM.yyyy", myCal.time).toString()
        NG_Utils.NG_Val_Wifi.lNuPiWifiRevVal(lValNumPicker)
        NG_Utils.NG_Val_Wifi.lWifiOffset(lMessung)
        NG_Val_Wifi.sWifiStartDate(sDate)
        sNewSummText = NG_Utils.MakeOutString(lValNumPicker.toDouble()) + " " + NG_Utils.CalcUnit(lValNumPicker.toDouble())
        myPrefWlanDate?.setSummary(sDate);
        myPrefWlanData?.setSummary(sNewSummText);


    }
} // End of class Cpc_SettingsFragment





