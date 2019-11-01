package de.franky.l.capricornng

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log

import java.util.ArrayList

/**
 * Created by franky on 08.09.2019.
 */

class NG_SharePref
//
//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
//    {
//        Log.d("onSharedPrefChanged",key);
//    }


internal constructor()
//public class NG_SharePref implements SharedPreferences.OnSharedPreferenceChangeListener
{

    private val NG_Pref: SharedPreferences
    private val NG_Editor: SharedPreferences.Editor
    private val NG_Ctxt: Context

    init {
        NG_Ctxt = NG_Application.applicationContext()
        NG_Pref = PreferenceManager.getDefaultSharedPreferences(NG_Ctxt)
        NG_Editor = NG_Pref.edit()
        //NG_Pref.registerOnSharedPreferenceChangeListener(this);
    }


    internal fun getDbl(sSearch: String, dDefault: Double): Double {
        var dResult: Double
        try {
            dResult = java.lang.Double.longBitsToDouble(
                NG_Pref.getLong(
                    sSearch,
                    java.lang.Double.doubleToRawLongBits(dDefault)
                )
            ) // Hole boole Wert aus sharedpreference
        } catch (e: Exception) {
            dResult =
                dDefault                                                    // Falls es kracht (ich weiss zwar nicht warum) setze Defaultwert
        }

        return dResult
    }

    internal fun putDbl(sSearch: String, dVal: Double) {
        try {
            NG_Editor.putLong(
                sSearch,
                java.lang.Double.doubleToRawLongBits(dVal)
            )                    // Setze double Wert in sharedpreference
            NG_Editor.apply()
        } catch (e: Exception) {
            Log.e(
                "Cpc_SharePref",
                "putDbl Problem $sSearch : $dVal"
            )        // Falls es kracht (ich weiss zwar nicht warum) error loggen
        }

    }

    internal fun getInt_R_ID(RIdSearch: Int, RIdDefault: Int): Int {
        var iResult: Int
        var iDef_Val = 0
        val sDef_Val: String

        if (RIdDefault > 0) {
            sDef_Val = NG_Ctxt.getString(RIdDefault)
            iDef_Val = sDef_Val.toInt()                        // Mache einen Integerwert draus
        }

        try {
            iResult = getInt(RIdSearch, iDef_Val)
        } catch (e: Exception) {
            iResult =
                iDef_Val                                                        // Falls es kracht (ich weiss zwar nicht warum) setze Defaultwert
        }

        return iResult

    }

    internal fun getInt(iSearch: Int, iDefault: Int): Int {
        return getInt(NG_Ctxt.getString(iSearch), iDefault)
    }

    internal fun getInt(sSearch: String, iDefault: Int): Int {
        var iResult: Int
        try {
            iResult = NG_Pref.getInt(
                sSearch,
                iDefault
            )                        // Hole boole Wert aus sharedpreference
        } catch (e: Exception) {
            iResult =
                iDefault                                                    // Falls es kracht (ich weiss zwar nicht warum) setze Defaultwert
        }

        return iResult
    }


    internal fun putInt(sSearch: String, iVal: Int) {
        try {
            NG_Editor.putInt(sSearch, iVal )                                    // Setze integer Wert in sharedpreference
            NG_Editor.apply()
        } catch (e: Exception) {
            Log.e(
                "Cpc_SharePref",
                "putInt Problem $sSearch : $iVal"
            )                                                    // Falls es kracht (ich weiss zwar nicht warum) error loggen
        }

    }

    internal fun getBool(iSearch: Int, bDefault: Boolean): Boolean {
        return getBool(NG_Ctxt.getString(iSearch), bDefault)
    }

    internal fun getBool(sSearch: String, bDefault: Boolean): Boolean {
        var bResult: Boolean
        try {
            bResult = NG_Pref.getBoolean(
                sSearch,
                bDefault
            )                    // Hole boole Wert aus sharedpreference
        } catch (e: Exception) {
            bResult =
                bDefault                                                    // Falls es kracht (ich weiss zwar nicht warum) setze Defaultwert
        }

        return bResult
    }

    internal fun putBool(sSearch: String, bVal: Boolean) {
        try {
            NG_Editor.putBoolean(
                sSearch,
                bVal
            )                                // Setze boole Wert in sharedpreference
            NG_Editor.apply()
        } catch (e: Exception) {
            Log.e(
                "Cpc_SharePref",
                "putBool Problem $sSearch : $bVal"
            )        // Falls es kracht (ich weiss zwar nicht warum) error loggen
        }

    }

    internal fun putString(sSearch: String, sVal: String) {
        try {
            NG_Editor.putString(
                sSearch,
                sVal
            )                                // Setze String Wert in sharedpreference
            NG_Editor.apply()
        } catch (e: Exception) {
            Log.e(
                "Cpc_SharePref",
                "putString Problem $sSearch : $sVal"
            )    // Falls es kracht (ich weiss zwar nicht warum) error loggen
        }

    }

    internal fun getString(sSearch: String, sDefault: String): String {
        var sResult: String
        try {
            sResult = NG_Pref.getString(
                sSearch,
                sDefault
            )                    // Hole String aus sharedpreference
        } catch (e: Exception) {
            sResult =
                sDefault                                                    // Falls es kracht (ich weiss zwar nicht warum) setze Defaultwert
        }

        return sResult
    }

    internal fun getString(iSearch: Int, iDefault: Int): String? {
        return getString(NG_Ctxt.getString(iSearch), NG_Ctxt.getString(iDefault))
    }

    internal fun putLong(sSearch: String, lVal: Long) {
        try {
            NG_Editor.putLong(
                sSearch,
                lVal
            )                                    // Setze boole Wert in sharedpreference
            NG_Editor.apply()
        } catch (e: Exception) {
            Log.e(
                "Cpc_SharePref",
                "putLong Problem with: $sSearch : $lVal"
            )        // Falls es kracht (ich weiss zwar nicht warum) error loggen
        }

    }

    internal fun getLong(iR_Search: Int, lDefault: Long): Long {
        return getLong(NG_Ctxt.getString(iR_Search), lDefault)
    }

    internal fun getLong(sSearch: String, lDefault: Long): Long {
        var lResult: Long
        try {
            lResult = NG_Pref.getLong(
                sSearch,
                lDefault
            )                        // Hole boole Wert aus sharedpreference
        } catch (e: Exception) {
            lResult =
                lDefault                                                    // Falls es kracht (ich weiss zwar nicht warum) setze Defaultwert
        }

        return lResult
    }

    internal fun getLong(iR_Search: Int, iRDefault: Int): Long {
        var lDef_Val = iRDefault.toLong()
        if (iRDefault > 0) {
            lDef_Val = NG_Ctxt.getString(iRDefault).toLong()                       // Mache einen Integerwert draus
        }
        return getLong(iR_Search, lDef_Val)
    }

    fun putStringArray(sSearch: String, array: ArrayList<String>): Boolean {
        try {
            NG_Editor.putInt(sSearch + "_size", array.size)
            for (i in array.indices) {
                NG_Editor.putString(sSearch + "_" + i, array[i])
            }
            NG_Editor.apply()
            return true
        } catch (e: Exception) {
            Log.e(
                "Cpc_SharePref",
                "putStringArray Problem $sSearch : $array"
            )                                                    // Falls es kracht (ich weiss zwar nicht warum) error loggen
            return false
        }

    }

    fun getStringArray(sR_Search: String): ArrayList<String> {
        val sReturn = ArrayList<String>()
        val size = NG_Pref.getInt(sR_Search + "_size", 0)
        for (i in 0 until size) {
            sReturn.add(NG_Pref.getString(sR_Search + "_" + i, "noNumber"))
        }
        return sReturn
    }
}
