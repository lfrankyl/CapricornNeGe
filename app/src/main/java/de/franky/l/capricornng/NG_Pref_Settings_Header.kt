package de.franky.l.capricornng

import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceFragmentCompat

class NG_Pref_Settings_Header:PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.ng_pref_settings_header,rootKey)
    }

}