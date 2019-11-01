package de.franky.l.capricornng

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceActivity
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat


class NG_Activity_SettingsHeader : AppCompatActivity(),PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
//implements ActivityCompat.OnRequestPermissionsResultCallback
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        supportFragmentManager.beginTransaction().replace(android.R.id.content,NG_Pref_Settings_Header()).commit();

    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat,pref: Preference ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment
        ).apply {
            arguments = args
            setTargetFragment(caller, 0)
        }
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
        title = pref.title
        return true
    }

}