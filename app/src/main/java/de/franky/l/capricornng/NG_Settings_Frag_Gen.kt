package de.franky.l.capricornng


import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat

import de.franky.l.capricornng.NG_Utils.NG_Pref
import java.util.*

class NG_Settings_Frag_Gen : PreferenceFragmentCompat() {
    private var sLocalSetting: String? = null
    internal var sBckgrdOptions = NG_Application.applicationContext().getResources().getStringArray(R.array.pref_Background)
    internal var sWdgtFontCol =   NG_Application.applicationContext().getResources().getStringArray(R.array.pref_WidgetFontColor)

    var myNumberPickerTransp :Preference? = null
    private var Lout_Container: ViewGroup? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?,rootKey: String?) {

        // Load the preferences from an XML resource
        // Log.d("Cpc_SettingsFragment","On create");
        setPreferencesFromResource(R.xml.ng_setting_frag_gen,rootKey)

        var sCurrentSummText: String
        // Landeseinstellung des Geraetes abrufen
        sLocalSetting = Locale.getDefault().displayName

        // Einstellungen fuer Aktualisierungsintervall
        val myPrefIntervall : ListPreference? =  findPreference (getString(R.string.pref_Intervall_Key))
        sCurrentSummText =
            NG_Utils.MakeOutString(myPrefIntervall!!.value.toDouble() / 1000) + getString(R.string.ng_blankChr ) + getString(R.string.pref_Intervall_Unit)
        myPrefIntervall.summary = sCurrentSummText
        myPrefIntervall.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, newValue ->
                val sNewSummText: String
                // Log.d("onPreferenceChange",getString(R.string.pref_Key_Intervall));
                sNewSummText =
                    NG_Utils.MakeOutString(java.lang.Double.parseDouble(newValue as String) / 1000) + getString(
                        R.string.ng_blankChr
                    ) + getString(R.string.pref_Intervall_Unit)
                preference.summary = sNewSummText
                true
            }

        // Einstellungen fuer Textgroesse Widget
        val myPrefTextSize : ListPreference? = findPreference(getString(R.string.pref_TextSize_Key))
        sCurrentSummText =
            NG_Utils.MakeOutString(myPrefTextSize!!.value.toDouble()) + getString(R.string.ng_blankChr) + getString(
                R.string.pref_TextSize_Unit
            )
        myPrefTextSize.summary = sCurrentSummText
        myPrefTextSize.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, newValue ->
                val sNewSummText: String
                // Log.d("onPreferenceChange",getString(R.string.pref_Key_Intervall));
                sNewSummText =
                    NG_Utils.MakeOutString(newValue.toString().toDouble()) + getString(R.string.ng_blankChr) + getString(
                        R.string.pref_TextSize_Unit
                    )
                preference.summary = sNewSummText
                true
            }
        // Einstellungen fuer Textgroesse App
        val myPrefAppTextSize : ListPreference? = findPreference (getString(R.string.pref_TextSizeApp_Key))
        sCurrentSummText =
            NG_Utils.MakeOutString(myPrefAppTextSize!!.value.toDouble()) + getString(R.string.ng_blankChr) + getString(R.string.pref_TextSize_Unit )
        myPrefAppTextSize.summary = sCurrentSummText
        myPrefAppTextSize.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, newValue ->
                val sNewSummText: String
                // Log.d("onPreferenceChange",getString(R.string.pref_Key_Intervall));
                sNewSummText =
                    NG_Utils.MakeOutString(newValue.toString().toDouble()) + getString(R.string.ng_blankChr) + getString(
                        R.string.pref_TextSize_Unit
                    )
                preference.summary = sNewSummText
                true
            }

        // Einstellungen fuer Hintergrund
        val myPrefBackground : ListPreference? = findPreference(getString(R.string.pref_Background_Key))
        var iCurVal = Integer.parseInt(myPrefBackground!!.value)
        if (iCurVal >= sBckgrdOptions.size) {
            iCurVal =
                Integer.parseInt(NG_Application.applicationContext().getResources().getString(R.string.pref_Background_Default))
        }
        sCurrentSummText = sBckgrdOptions[iCurVal]

        myPrefBackground.summary = sCurrentSummText
        myPrefBackground.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, newValue ->
                val sNewSummText: String
                // Log.d("onPreferenceChange",getString(R.string.pref_Key_Intervall));
                sNewSummText = sBckgrdOptions[Integer.parseInt(newValue as String)]
                preference.summary = sNewSummText
                true
            }

        // Einstellungen fuer Widget Schriftfarbe
        val myPrefWidgetFontColor : ListPreference? = findPreference(getString(R.string.pref_WdgtFontcolor_Key))
        sCurrentSummText = sWdgtFontCol[Integer.parseInt(myPrefWidgetFontColor!!.value)]
        myPrefWidgetFontColor.summary = sCurrentSummText
        myPrefWidgetFontColor.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, newValue ->
                val sNewSummText: String
                // Log.d("onPreferenceChange",getString(R.string.pref_Key_Intervall));
                sNewSummText = sWdgtFontCol[Integer.parseInt(newValue as String)]
                preference.summary = sNewSummText
                true
            }

        // Einstellungen fuer Numberpicker Transparenz des Widgethintergrundds
        //Log.d("myNumberPickerTransp",	 myNumberPickerTransp.getKey());
        myNumberPickerTransp = findPreference(getString(R.string.pref_NuPi_BckgrdTransp_Key))
        val lCurrentSummText =
            NG_Pref.getLong(R.string.pref_NuPi_BckgrdTransp_Key, R.string.pref_BckgrdTransp_Default)
        sCurrentSummText = lCurrentSummText.toString() + " " + NG_Application.applicationContext().getResources().getString(R.string.pref_BckgrdTransp_Unit)
        myNumberPickerTransp?.setSummary(sCurrentSummText)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Lout_Container = view as ViewGroup
    }
    override fun onStart() {
        super.onStart()
        // Log.d("onStart","onStart");
        val sNewSummText: String
        val myPrefLang: ListPreference? = findPreference(getString(R.string.pref_Language_Key))
        sNewSummText = getString(R.string.pref_Language_Sum) + " " + sLocalSetting

        myPrefLang!!.summary = sNewSummText
        NG_Utils.changeTextSize(Lout_Container)
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
                    val sUnitValues = arrayOf ("%")

                    val iNupiDataMax = 100
                    val iNupiDataMin = 0

                    val iNupiUnitMax = sUnitValues.size - 1
                    val iNupiUnitMin = 0

                    // Log.d("Cpc_Dlg_NuPi_MobData_RevVal","onCreateDialogView");


                    val mTV = it.findViewById(R.id.tv_nupi_refval_explanation) as TextView;
                    mTV.setText(NG_Application.applicationContext().getResources().getString(R.string.pref_BckgrdTransp_Expl))

                    dialogFragment.PickerData = it.findViewById(R.id.nupi_refval_value)

                    // Initialize state
                    dialogFragment.PickerData!!.maxValue = iNupiDataMax
                    dialogFragment.PickerData!!.minValue = iNupiDataMin

                    // Log.d("onCreateDialogView",String.valueOf(iwlanDataDefVal));

                    //Log.d("onCreateDialogView",String.valueOf(iWlanPickerValueNeu));
                    dialogFragment.PickerData!!.wrapSelectorWheel = true
                    dialogFragment.PickerData!!.value =NG_Pref.getLong(getString(R.string.pref_NuPi_BckgrdTransp_Key), 50).toInt()
                    dialogFragment.PickerUnit = it.findViewById(R.id.nupi_refval_unit)

                    // Initialize state
                    dialogFragment.PickerUnit!!.maxValue = iNupiUnitMax
                    dialogFragment.PickerUnit!!.minValue = iNupiUnitMin
                    dialogFragment.PickerUnit!!.wrapSelectorWheel = false  // Works only on SKD >=16,
                    dialogFragment.PickerUnit!!.displayedValues = sUnitValues

                    dialogFragment.PickerUnit!!.value = NG_Pref.getLong(R.string.pref_NuPi_BckgrdTransp_Key, R.string.pref_BckgrdTransp_Default).toInt()

                }
        }
        else
        {
            super.onDisplayPreferenceDialog(preference)
        }

    }

    private fun SetAfterPositivDialog(lValNumPicker:Long){
        NG_Pref.putLong(getString(R.string.pref_NuPi_BckgrdTransp_Key), lValNumPicker)
        val sNewSummText: String
        sNewSummText = lValNumPicker.toString()  + NG_Application.applicationContext().getResources().getString(R.string.pref_BckgrdTransp_Unit)
        myNumberPickerTransp?.setSummary(sNewSummText);
    }

} // End of class Cpc_SettingsFragment





