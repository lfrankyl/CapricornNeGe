package de.franky.l.capricornng


import android.app.AlertDialog
import android.content.SharedPreferences
import android.net.TrafficStats
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.widget.TextView
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference

import java.util.Calendar
import java.util.GregorianCalendar


class NG_Settings_Frag_Cost_Rel_Data : PreferenceFragmentCompat()  {

    private var DefSharedPref: SharedPreferences? = null

    var myPrefMobileOptions: ListPreference? = null       // muss hier definiert werden damit über alle Klassenfunktionen bekannt
    var myNumberPickerCycle: Preference? =    null        // 				"
    var myMobileCycleOption: ListPreference? = null       // 				"
    var myNumberPickerPref: Preference? = null            // 				"
    var myNumberPickerMaxVal: Preference? = null          // 				"
    var myPrefMobViewType: SwitchPreference? = null       // 				"
    var myPrefMobileMonitoring : SwitchPreference? = null // 				"
    var myPrefCallsScreen : Preference? = null            //                "

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.ng_setting_frag_cost_rel_data, rootKey)

        DefSharedPref = PreferenceManager.getDefaultSharedPreferences(NG_Application.applicationContext())

        myPrefMobileOptions       = findPreference(getString(R.string.pref_MobileOptions_Key))
        myNumberPickerCycle       = findPreference(getString(R.string.pref_NuPiCycle_Key))
        myMobileCycleOption       = findPreference(getString(R.string.pref_MobileCycleOption_Key))
        myNumberPickerPref        = findPreference(getString(R.string.pref_NuPiRef_Mob_Key))
        myNumberPickerMaxVal      = findPreference(getString(R.string.pref_NuPiMax_Mob_Key))
        myPrefMobViewType         = findPreference(getString(R.string.pref_MobileView_Key))
        myPrefMobileMonitoring    = findPreference(getString(R.string.pref_MobileMonitoring_Key))
        myPrefCallsScreen         = findPreference(getString(R.string.pref_Calls_Screen_Key))


        // Einstellungen fuer Datenberechnung: ohne, Referenzwert, Abrechnungszeitraum
        // Muss am Ende des OnCreate passieren sonst überschreibt man die Einstellungen wieder
        val sMobileOptions = NG_Application.applicationContext().getResources().getStringArray(R.array.mobileOptionsArray)
        val iCurValue = NG_Utils.NG_Val_Time_Cots.iDataCalulation()
        var sCurrentSummText = sMobileOptions[iCurValue]
        SetOptionsDependingOnDataCalculationOption(iCurValue)
        myPrefMobileOptions!!.summary = sCurrentSummText
        myPrefMobileOptions!!.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
            val sMobileOptions = NG_Application.applicationContext().getResources().getStringArray(R.array.mobileOptionsArray)
            val iNewValue = (newValue as String).toInt()

            SetOptionsDependingOnDataCalculationOption(iNewValue)
            val sNewSummText: String
            sNewSummText = sMobileOptions[iNewValue]
            NG_Utils.NG_Val_Time_Cots.iDataCalulation(iNewValue)
            preference.summary = sNewSummText
            true
        }


        // Einstellungen fuer Numberpicker fuer eigenen Zyklus
        val myCal = GregorianCalendar()
        val iCurrentSummText = NG_Utils.NG_Val_Time_Cots.iNuPiCycle()
        val iCurrentMonth = myCal.get(Calendar.MONTH)      // Aktuellen Monat merken
        // Log.d("NuPi original I",String.valueOf(iCurrentSummText));
        if (NG_Utils.NG_Val_Time_Cots.iMobileCycleOption() > 0)
        // Wenn 30 oder 28-Tage-Zyklus
        {
            if (iCurrentSummText < myCal.get(Calendar.DAY_OF_MONTH) || iCurrentSummText <= myCal.get(Calendar.DAY_OF_MONTH) && NG_Utils.NG_Val_Time_Cots.bMobResetIsLocked())
            {
                myCal.roll(Calendar.MONTH, 1)
                if (myCal.get(Calendar.MONTH) < iCurrentMonth)
                // falls neuer Monat kleiner als aktueller Monat dann
                {
                    myCal.roll(Calendar.YEAR, 1)              // Jahr auch um 1 nach vorne drehen
                }
            }
        } else
        // wenn monatlicher Zyklus
        {
            if (iCurrentSummText <= myCal.get(Calendar.DAY_OF_MONTH))
            {
                myCal.roll(Calendar.MONTH, 1)
                if (myCal.get(Calendar.MONTH) < iCurrentMonth)
                // falls neuer Monat kleiner als aktueller Monat dann
                {
                    myCal.roll(Calendar.YEAR, 1)              // Jahr auch um 1 nach vorne drehen
                }
            }
        }
        myCal.set(Calendar.DAY_OF_MONTH, iCurrentSummText)

        val sDate = android.text.format.DateFormat.format("dd-MM-yyyy", myCal.time).toString()
        // Log.d("NuPi original II",sDate);
        sCurrentSummText = getString(R.string.pref_MobileCycle_Sum1) + getString(R.string.ng_blankChr) + sDate
        myNumberPickerCycle?.summary = sCurrentSummText


        // Einstellungen fuer Zyklus Typ
        val sMobileCycleOptions =  NG_Application.applicationContext().getResources().getStringArray(R.array.MobileCycleOptionArray)
        sCurrentSummText = getString(R.string.pref_MobileCycleOption_Sum) + getString(R.string.ng_blankChr) + sMobileCycleOptions[NG_Utils.NG_Val_Time_Cots.iMobileCycleOption()]
        myMobileCycleOption!!.summary = sCurrentSummText
        myMobileCycleOption!!.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, newValue ->
                val sMobileCycleOptions = NG_Application.applicationContext().getResources().getStringArray(R.array.MobileCycleOptionArray)
                val iNewValue = Integer.parseInt(newValue as String)
                //       NG_Utils.CurVal.iMobileCycleTyp = iNewValue
                // Log.d("onPChg bMobileCycleTyp",String.valueOf(iNewValue));
                val sNewSummText: String
                // Log.d("onPreferenceChange",getString(R.string.pref_Key_viewLeft));
                sNewSummText = getString(R.string.pref_MobileCycleOption_Sum) + getString(R.string.ng_blankChr) + sMobileCycleOptions[iNewValue]
                NG_Utils.NG_Val_Time_Cots.iMobileCycleOption(iNewValue )
                preference.summary = sNewSummText
                true
            }


        //  Allgemein Ende
        // ---------------------------------------------------------------------------------------------
        //
        //  Mobile Data Begin

        // Einstellungen fuer Mobile Daten monitoren Ja/Nein
        //
        sCurrentSummText = SetMonitoringResultData(false, myNumberPickerMaxVal!!, myPrefMobViewType as Preference,null,null, myPrefMobileMonitoring!!.isChecked)
        myPrefMobileMonitoring!!.summary = sCurrentSummText
        myPrefMobileMonitoring!!.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, newValue ->
                val sNewSummText = SetMonitoringResultData(false,myNumberPickerMaxVal!!, myPrefMobViewType!!,null,null,newValue == true )
                // Log.d("onPreferenceChange",getString(R.string.pref_MobileMonitoring_Key));
                preference.summary = sNewSummText
                true
            }

        // Einstellungen fuer Numberpicker Referenzwert mobile Daten
        //
        var lCurrentSummText = NG_Utils.NG_Val_Mob_Data.lNuPiMobRevVal()
        sCurrentSummText = NG_Utils.MakeOutString(lCurrentSummText.toDouble())
        sCurrentSummText = sCurrentSummText + " " + NG_Utils.CalcUnit(lCurrentSummText.toDouble())
        myNumberPickerPref?.summary = sCurrentSummText

        // Einstellungen fuer Numberpicker Maxwert mobile Daten
        //
        lCurrentSummText = NG_Utils.NG_Val_Mob_Data.lNuPiMobMaxVal()
        sCurrentSummText = NG_Utils.MakeOutString(lCurrentSummText.toDouble())
        sCurrentSummText = sCurrentSummText + " " + NG_Utils.CalcUnit(lCurrentSummText.toDouble())
        myNumberPickerMaxVal?.summary = sCurrentSummText

        // Einstellungen fuer Anzeige mobile Daten verbrauchte oder noch uebrige
        //
        if (myPrefMobViewType!!.isChecked) {
            sCurrentSummText = getString(R.string.pref_MobileView_SumOn)
        } else {
            sCurrentSummText = getString(R.string.pref_MobileView_SumOff)
        }
        myPrefMobViewType!!.summary = sCurrentSummText
        myPrefMobViewType!!.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, newValue ->
                if (newValue == true) {
                    preference.summary = getString(R.string.pref_MobileView_SumOn)
                } else {
                    preference.summary = getString(R.string.pref_MobileView_SumOff)
                }
                // Log.d("onPreferenceChange",getString(R.string.pref_MobileView_Key));
                true
            }



        //  Mobile Data Ende
        // ---------------------------------------------------------------------------------------------
        //
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


                    if (preference.key.equals(getString(R.string.pref_NuPiRef_Mob_Key))){
                        dialogFragment.PickerUnit!!.wrapSelectorWheel = true
                        sUnitValues = getResources().getStringArray(R.array.pref_DataUnits)

                        dialogFragment.PickerData!!.maxValue = 9999
                        dialogFragment.PickerData!!.minValue = 0

                        val lCurrentSummText = NG_Utils.NG_Val_Mob_Data.lNuPiMobRevVal()
                        dialogFragment.PickerData!!.value =NG_Utils.iCalcDataVal(lCurrentSummText)
                        iUnitIndex = NG_Utils.iCalcDataUnitIndex(lCurrentSummText)
                        mTV.setText(getString(R.string.pref_MobileRefVal_Explanation))

                    }
                    else if (preference.key.equals(getString(R.string.pref_NuPiMax_Mob_Key))){
                        dialogFragment.PickerUnit!!.wrapSelectorWheel = true
                        sUnitValues = getResources().getStringArray(R.array.pref_DataUnits)

                        dialogFragment.PickerData!!.maxValue = 9999
                        dialogFragment.PickerData!!.minValue = 0

                        val lCurrentSummText = NG_Utils.NG_Val_Mob_Data.lNuPiMobMaxVal()
                        dialogFragment.PickerData!!.value =NG_Utils.iCalcDataVal(lCurrentSummText)
                        iUnitIndex =NG_Utils.iCalcDataUnitIndex(lCurrentSummText)
                        mTV.setText(getString(R.string.pref_MobileMaxVal_Expl))
                    }
                    else
                    {   // For the reset date dialog
                        sUnitValues = arrayOf (getString(R.string.pref_MobileCycle_Unit))
                        dialogFragment.PickerUnit!!.wrapSelectorWheel = false
                        dialogFragment.PickerData!!.maxValue = 31
                        dialogFragment.PickerData!!.minValue = 1
                        dialogFragment.PickerData!!.value =NG_Utils.NG_Val_Time_Cots.iNuPiCycle()
                        mTV.setText(getString(R.string.pref_MobileCycle_Explanation))
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
        if (sWhichDialog.equals(getString(R.string.pref_NuPiCycle_Key))) {
            if (myCal.get(Calendar.DAY_OF_MONTH) > lValNumPicker || myCal.get(Calendar.DAY_OF_MONTH) >= lValNumPicker  && NG_Utils.NG_Val_Time_Cots.bMobResetIsLocked())
            {
                myCal.roll(Calendar.MONTH, 1)
                if (myCal.get(Calendar.MONTH) < iCurrentMonth)
                // falls neuer Monat kleiner als aktueller Monat dann
                {
                    myCal.roll(Calendar.YEAR, 1)              // Jahr auch um 1 nach vorne drehen
                }
            }
            // Log.d("NuPi changed iMobileNumberPicker",String.valueOf(iMobileNumberPicker));
            myCal.set(Calendar.DAY_OF_MONTH, lValNumPicker.toInt())
            sDate = android.text.format.DateFormat.format("dd-MM-yyyy", myCal.time).toString()
            // Log.d("NuPi changed sDate",String.valueOf(iMobileNumberPicker));
            sNewSummText = getString(R.string.pref_MobileCycle_Sum1) + getString(R.string.ng_blankChr) + sDate
            myNumberPickerCycle?.setSummary(sNewSummText);
            NG_Utils.NG_Val_Time_Cots.iNuPiCycle(lValNumPicker.toInt())
            //Log.d("NuPi changed sDate",String.valueOf(NG_Utils.CurVal.iMobileCycleTyp));

            val iMobileCycleOptions = NG_Application.applicationContext().getResources().getIntArray(R.array.MobileCycleOptionArray_Values)
            if (myMobileCycleOption!!.value.toInt() > 0 && !NG_Utils_Data_Constraints.IsADayWithinDaysRange(
                    lValNumPicker.toInt(),
                    iMobileCycleOptions[myMobileCycleOption!!.value.toInt()]
                )
            ) {
                val DialogBuilder = AlertDialog.Builder(activity)

                DialogBuilder.setTitle(R.string.pref_MobileCycle_XXDays2Long_Title)
                DialogBuilder.setMessage(NG_Application.applicationContext().getResources().getString(R.string.pref_MobileCycle_XXDays2Long_Message1) + NG_Application.applicationContext().getResources().getString(
                    R.string.ng_blankChr
                )
                ) //+ iMobileCycleOptions[NG_Utils.CurVal.iMobileCycleTyp].toString()+ NG_Application.applicationContext().getResources().getString(R.string.pref_MobileCycle_XXDays2Long_Message2))
                DialogBuilder.setPositiveButton(android.R.string.ok) { dialog, which ->
                    // Do nothing but close the dialog
                    dialog.dismiss()
                }
                val alert = DialogBuilder.create()
                Log.d("NuPi changed sDate", "Jetzt sollte der Dialog kommen")
                alert.show()
            }
        }
        else if(sWhichDialog.equals(getString(R.string.pref_NuPiRef_Mob_Key))){
            sNewSummText = NG_Utils.MakeOutString(lValNumPicker.toDouble()) + " " + NG_Utils.CalcUnit(lValNumPicker.toDouble())
            NG_Utils.NG_Val_Mob_Data.lNuPiMobRevVal(lValNumPicker)
            myNumberPickerPref?.summary = sNewSummText
            NG_Utils.NG_Val_Wifi.bWIFISetup(NG_Utils.NG_Val_Wifi.IsWLANandLollipop())
            if (NG_Utils.NG_Val_Wifi.IsWLANandLollipop()) {
                Log.i("pref_NuPiRef_Mob_Key", "pref_WlanSetup_Key -> true")
            } else {
                val lMessung = TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes()
                NG_Utils.NG_Val_Mob_Data.lMobileOffsetMessung(lMessung)
                //Log.d("pref_NuPiRef_Mob_Key", "pref_WlanSetup_Key -> false " + String.valueOf(lMessung));
            }
        }
        else if(sWhichDialog.equals(getString(R.string.pref_NuPiMax_Mob_Key))){
            sNewSummText = NG_Utils.MakeOutString(lValNumPicker.toDouble()) + " " + NG_Utils.CalcUnit(lValNumPicker.toDouble())
            myNumberPickerMaxVal?.summary = sNewSummText
            NG_Utils.NG_Val_Mob_Data.lNuPiMobMaxVal(lValNumPicker)
        }
    }

    private fun SetOptions(bReference: Boolean, bCategory: Boolean, bMonitoring: Boolean) {

        SetPreferenceStatus(myNumberPickerCycle, bCategory)
        SetPreferenceStatus(myMobileCycleOption, bCategory)
        SetPreferenceStatus(myNumberPickerPref, bReference)
        SetPreferenceStatus(myPrefMobileMonitoring, bMonitoring)
        SetPreferenceStatus(myNumberPickerMaxVal, bMonitoring && myPrefMobileMonitoring!!.isChecked)
        SetPreferenceStatus(myPrefMobViewType, bMonitoring && myPrefMobileMonitoring!!.isChecked)

        if (BuildConfig.WithCalls){
            SetPreferenceStatus(myPrefCallsScreen,bReference)
        }
        else
        {
            myPrefCallsScreen!!.isVisible = false
        }
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
} // End of class Cpc_Settings_Frag_Cost_Rel_Data
