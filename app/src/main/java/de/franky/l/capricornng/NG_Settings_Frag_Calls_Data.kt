package de.franky.l.capricornng


import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.preference.*
import androidx.preference.Preference.OnPreferenceChangeListener

import java.util.Calendar
import java.util.GregorianCalendar

import de.franky.l.capricornng.NG_Utils.NG_Pref

class NG_Settings_Frag_Calls_Data : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener  {

    private var DefSharedPref: SharedPreferences? = null

    var myNumberPickerMaxCalls: Preference? = null         // muss hier definiert werden damit über alle Klassenfunktionen bekannt
    var myNumberPickerRefValCalls: Preference? = null      // 				"
    var free_PhoneNumber: Preference? = null               // 				"
    var myPrefCallsViewType : SwitchPreference?    = null  // 				"
    var myPrefCallsIntegrateSMS : SwitchPreference? =null  // 				"
    var myPrefCallsMonitoring : SwitchPreference? =null    // 				"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        // Load the preferences from an XML resource
        // Log.d("Cpc_Settings_Frag_Cost_Rel_Data","On create");
        setPreferencesFromResource(R.xml.ng_setting_frag_calls_data, rootKey)

        DefSharedPref = PreferenceManager.getDefaultSharedPreferences(NG_Application.applicationContext())

        myNumberPickerMaxCalls    = findPreference(getString(R.string.pref_NuPiMax_Calls_Key))
        myNumberPickerRefValCalls = findPreference(getString(R.string.pref_NuPiRef_Calls_Key))
        free_PhoneNumber          = findPreference(getString(R.string.pref_Calls_FreeNumb_Key))
        myPrefCallsViewType       = findPreference(getString(R.string.pref_Calls_View_Key))
        myPrefCallsIntegrateSMS   = findPreference(getString(R.string.pref_CallsIntegrateSMS_Key))
        myPrefCallsMonitoring     = findPreference(getString(R.string.pref_CallsMonitoring_Key))

        // ---------------------------------------------------------------------------------------------
        //
        //  Calls Begin

        // Einstellungen fuer Anrufe monitoren Ja/Nein
        var sCurrentSummText = SetMonitoringResultData(true, myNumberPickerMaxCalls!!,myPrefCallsViewType as Preference,myPrefCallsIntegrateSMS,free_PhoneNumber,myPrefCallsMonitoring!!.isChecked());
        myPrefCallsMonitoring!!.setSummary(sCurrentSummText);
        myPrefCallsMonitoring!!.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, newValue ->
                val sNewSummText = SetMonitoringResultData(true, myNumberPickerMaxCalls!!,myPrefCallsViewType as Preference,myPrefCallsIntegrateSMS, free_PhoneNumber,newValue.equals(true));
                // Log.d("onPreferenceChange",getString(R.string.pref_Key_MobileMonitoring));
                preference.summary = sNewSummText
                true;
            }

        // Einstellungen fuer kostenlose Telefonnummern
        sCurrentSummText = NG_Pref.getInt(R.string.pref_Calls_FreeNumb_Key,0).toString();
        sCurrentSummText = sCurrentSummText + " " + getString(R.string.pref_Calls_FreeNumb_Sum);
        free_PhoneNumber!!.setSummary(sCurrentSummText);


        // Einstellungen fuer SMS in Freiminuten integrieren oder nicht
        sCurrentSummText = getString(R.string.pref_CallsIntegrateSMS_SumOff) ;
        if (myPrefCallsIntegrateSMS!!.isChecked())
        {
            sCurrentSummText = getString(R.string.pref_CallsIntegrateSMS_SumOn) ;
        }
        myPrefCallsIntegrateSMS!!.setSummary(sCurrentSummText);
        myPrefCallsIntegrateSMS!!.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, newValue ->
                var sNewSummText = getString(R.string.pref_CallsIntegrateSMS_SumOff);
                if (newValue.equals(true))
                {
                    sNewSummText = getString(R.string.pref_CallsIntegrateSMS_SumOn) ;
                }
                // Log.d("onPreferenceChange",getString(R.string.pref_CallsIntegrateSMS_Key));
                preference.setSummary(sNewSummText);
                true;
            }

        // Einstellungen fuer Anzeige Anrufe verbrauchte oder noch uebrige
        //
        sCurrentSummText = getString(R.string.pref_Calls_View_SumOff) ;
        if (myPrefCallsViewType!!.isChecked())
        {
            sCurrentSummText = getString(R.string.pref_Calls_View_SumOn) ;
        }
        myPrefCallsViewType!!.setSummary(sCurrentSummText);
        myPrefCallsViewType!!.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, newValue ->
                var sNewSummText = getString(R.string.pref_Calls_View_SumOff);
                if (newValue.equals(true)) {
                    sNewSummText = getString(R.string.pref_Calls_View_SumOn);
                }
                // Log.d("onPreferenceChange",getString(R.string.pref_Calls_View_Key));
                preference.setSummary(sNewSummText);
                true;
            }

        // Einstellungen fuer Numberpicker Maxwert Freiminuten
        //
        var lCurrentSummText = NG_Pref.getLong(R.string.pref_NuPiMax_Calls_Key, R.string.pref_Calls_MaxVal_Default)
        sCurrentSummText = lCurrentSummText.toString() + " " + getString(R.string.pref_Calls_MaxVal_Unit)
        myNumberPickerMaxCalls?.summary = sCurrentSummText

        // Einstellungen fuer Numberpicker Referenzwert Freiminuten
        //
        lCurrentSummText = NG_Pref.getLong(R.string.pref_NuPiRef_Calls_Key, R.string.pref_Calls_RefVal_Default)
        sCurrentSummText = lCurrentSummText.toString() + " " + getString(R.string.pref_Calls_RefVal_Unit)
        myNumberPickerRefValCalls?.summary = sCurrentSummText

        // Finde heraus was an Optionen (keine, Referenz, Abrechnungszeitraum) eingestellt ist
        // Muss am Ende des OnCreate passieren sonst überschreibt man die Einstellungen wieder
        val iMobileOptions = NG_Utils.NG_Val_Time_Cots.iDataCalulation()
        SetOptionsDependingOnDataCalculationOption(iMobileOptions)
    }


    // This is the function called when a numberpickerdialog will be displayed
    override fun onDisplayPreferenceDialog(preference: Preference) {
        val clearRecentDialog = preference as? NG_Dlg_NuPi_Preference
        if (clearRecentDialog != null) {
            val dialogFragment = NG_Dlg_NuPi_Dialog.newInstance(clearRecentDialog.key)
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(fragmentManager!!, null)
            dialogFragment.positiveResult=
                {
                    val lNuPiValue = NG_Utils.lCalcDataValToByte(dialogFragment.PickerData!!.getValue().toLong(),dialogFragment.PickerUnit!!.getValue())
                    SetAfterPositivDialog(preference.key,lNuPiValue)
                }
            dialogFragment.CreateDialogView=
                {

                    dialogFragment.PickerData = it.findViewById(R.id.nupi_refval_value)
                    dialogFragment.PickerData!!.wrapSelectorWheel = true
                    dialogFragment.PickerUnit = it.findViewById(R.id.nupi_refval_unit)

                    var sUnitValues: Array<String>
                    var iUnitIndex = 0

                    val mTV = it.findViewById(R.id.tv_nupi_refval_explanation) as TextView;

                    dialogFragment.PickerData!!.maxValue = 1000
                    dialogFragment.PickerData!!.minValue = 0

                    if (preference.key.equals(getString(R.string.pref_NuPiMax_Calls_Key))){
                        dialogFragment.PickerUnit!!.wrapSelectorWheel = false
                        sUnitValues = arrayOf (getString(R.string.pref_Calls_MaxVal_Unit))


                        val lCurrentSummText = NG_Pref.getLong(R.string.pref_NuPiMax_Calls_Key,R.string.pref_Calls_MaxVal_Default)
                        dialogFragment.PickerData!!.value =NG_Utils.iCalcDataVal(lCurrentSummText)
                        iUnitIndex =0
                        mTV.setText(getString(R.string.pref_Calls_MaxVal_Expl))
                    }
                    else {
                        dialogFragment.PickerUnit!!.wrapSelectorWheel = false
                        sUnitValues = arrayOf (getString(R.string.pref_Calls_RefVal_Unit))

                        val lCurrentSummText = NG_Pref.getLong(R.string.pref_NuPiRef_Calls_Key,R.string.pref_Calls_RefVal_Default)
                        dialogFragment.PickerData!!.value =NG_Utils.iCalcDataVal(lCurrentSummText)
                        iUnitIndex = 0
                        mTV.setText(getString(R.string.pref_Calls_RefVal_Expl))
                    }
                    dialogFragment.PickerUnit!!.displayedValues = sUnitValues
                    dialogFragment.PickerUnit!!.maxValue =  sUnitValues.size-1
                    dialogFragment.PickerUnit!!.minValue =  0
                    dialogFragment.PickerUnit!!.value = iUnitIndex
                }
        }
        else
        {
            super.onDisplayPreferenceDialog(preference)
        }

    }


    // This is the function which is call after OK in Numberpickerdialogs
    //
    private fun SetAfterPositivDialog(sWhichDialog: String, lValNumPicker:Long){
        var sNewSummText: String
        val myCal = GregorianCalendar()
        val sDate: String
        val iCurrentMonth = myCal.get(Calendar.MONTH)      // Aktuellen Monat merken
        if (sWhichDialog.equals(getString(R.string.pref_NuPiMax_Calls_Key))){
            sNewSummText = lValNumPicker.toString() + " " + getString(R.string.pref_Calls_MaxVal_Unit)
            myNumberPickerMaxCalls?.summary = sNewSummText
            NG_Pref.putLong(getString(R.string.pref_NuPiMax_Calls_Key), lValNumPicker)
        }
        else {
            sNewSummText = lValNumPicker.toString() + " " + getString(R.string.pref_Calls_RefVal_Unit)
            myNumberPickerRefValCalls?.summary = sNewSummText
            NG_Pref.putLong(getString(R.string.pref_NuPiRef_Calls_Key), lValNumPicker)
        }
    }

    private fun SetOptions(bReference: Boolean, bCategory: Boolean, bMonitoring: Boolean) {

        SetPreferenceStatus(myPrefCallsMonitoring, bMonitoring )
        SetPreferenceStatus(myNumberPickerRefValCalls, bReference)
        SetPreferenceStatus(myNumberPickerMaxCalls, bMonitoring && myPrefCallsMonitoring!!.isChecked)
        SetPreferenceStatus(myPrefCallsViewType, bMonitoring && myPrefCallsMonitoring!!.isChecked)
        SetPreferenceStatus(myPrefCallsIntegrateSMS, bMonitoring && myPrefCallsMonitoring!!.isChecked)
        SetPreferenceStatus(free_PhoneNumber, bMonitoring && myPrefCallsMonitoring!!.isChecked)

        //NG_Utils.CurVal.bMobileDataCycle = bReference && bCategory && bMonitoring
    }

    private fun SetPreferenceStatus(myPref: Preference?, Status: Boolean) {
        myPref!!.isEnabled = Status
        myPref!!.isSelectable = Status
    }

    private fun SetMonitoringResultData(bCalls: Boolean, preference1: Preference, preference2: Preference, preference3: Preference?, preference4: Preference?, bStatus: Boolean ): String {
        var sResult = getString(R.string.pref_Monitoring_SumOff)
        try {
            SetPreferenceStatus(preference1, bStatus)
            SetPreferenceStatus(preference2, bStatus)
            if (preference3 != null)
                SetPreferenceStatus(preference3, bStatus)
            if (preference4 != null)
                SetPreferenceStatus(preference4, bStatus)
            if (bStatus == java.lang.Boolean.TRUE) {
                sResult = getString(R.string.pref_Monitoring_SumOn)
            }
        } catch (e: Exception) {
            sResult = "Shit MonitoringResultData"
        }

        return sResult
    }

    private fun SetOptionsDependingOnDataCalculationOption(iSelectedOption:Int){
        if (iSelectedOption == 1)
        // Referenzwert
        {
            // Log.d("pref_Key_mobData_Options on change",sMobileOptions[1]);
            SetOptions(true, false, false)
        } else {
            if (iSelectedOption == 2)
            // Zyklus
            {
                // Log.d("pref_Key_mobData_Options on change",sMobileOptions[2]);
                SetOptions(true, true, true)

            } else
            // ohne Anpassung
            {
                // Log.d("pref_Key_mobData_Options on change",sMobileOptions[0]);
                SetOptions(false, false, false)
            }
        }

    }


    override fun onResume() {
        super.onResume()
        DefSharedPref!!.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        DefSharedPref!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        DefSharedPref!!.unregisterOnSharedPreferenceChangeListener(this)
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        try {
            // Log.d("onSharedPreferenceChanged",	 key);
            if (key == NG_Application.applicationContext().getResources().getString(R.string.pref_Calls_FreeNumb_Key) || key == getString(
                    R.string.pref_Calls_FreeNumbArr_Key
                )
            ) {
                val iCurrentSummText = NG_Pref.getInt(
                    R.string.pref_Calls_FreeNumb_Key,
                    R.string.pref_Calls_FreeNumb_Default
                )
                var sCurrentSummText = iCurrentSummText.toString()
                // Log.d("free_PhoneNumber changed II",	 sCurrentSummText);
                sCurrentSummText =
                    sCurrentSummText + " " + getString(R.string.pref_Calls_FreeNumb_Sum)
                free_PhoneNumber!!.setSummary(sCurrentSummText)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText( NG_Application.applicationContext(), "Exception @ onSharedPreferenceChanged",  Toast.LENGTH_LONG ).show()

        }

    }
} // End of class Cpc_Settings_Frag_Cost_Rel_Data
