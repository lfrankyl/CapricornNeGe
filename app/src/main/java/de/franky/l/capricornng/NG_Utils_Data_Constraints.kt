package de.franky.l.capricornng

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import java.util.*

object NG_Utils_Data_Constraints{

    private val isNetworkOff: Boolean
        get() = networkType == -1
    private// Hole Systemdaten um zu erkennen ob
    val networkType: Int
        get() {
            val cm =  NG_Application.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var iReturn = -1
            val NwI = cm.activeNetworkInfo
            if (NwI != null) {
                iReturn = NwI.type
            }
            return iReturn
        }

    fun CalcMonitoringConstraints(context: Context): Boolean {

        // Log.d("Monitoring","Daten zum monitoren der mobilen Daten");
        //  Log.d("Monitoring debugging","Start -------------------------");
        var bAllesOk: Boolean? = true  // Rueckgabewert der Funktion
        try {
            var iCorrectionDays = 0                                // Anzahl Korrekturtage beim Jahreswechsel
            val iMobileCycleTyp = NG_Utils.NG_Pref.getString( R.string.pref_MobileCycleOption_Key,  R.string.pref_MobileCycleOption_Default)?.toInt()

            val cal_Today = GregorianCalendar.getInstance()
            val iTodayDayOfMonth = cal_Today.get(Calendar.DAY_OF_MONTH)    // Das ist heute bezogen auf Monat 1 - 31
            val iTodayMonth =      cal_Today.get(Calendar.MONTH)                // Das ist der heutige Monat 1 - 12
            val iTodayDayOfYear =  cal_Today.get(Calendar.DAY_OF_YEAR)        // Heute als Tag im Jahre 1 - 36x
            var cal_change = GregorianCalendar.getInstance()
            val iChangeDayOfMonth = NG_Utils.NG_Val_Time_Cots.iNuPiCycle()    // Tag fuer den Reset der mobilen Daten
            cal_change.set(Calendar.DAY_OF_MONTH, iChangeDayOfMonth)
            if (iMobileCycleTyp!! > 0)
            // wenn 30 oder 28 Tage eingestellt ist
            {
                if (iTodayDayOfMonth > iChangeDayOfMonth ||                                                // wenn heutiger Tag im Monat groesser als der Aenderungstag oder
                    iTodayDayOfMonth >= iChangeDayOfMonth && NG_Utils.NG_Val_Time_Cots.bMobResetIsLocked()
                )
                // der Tag groesser/gleich und die Daten zurueckgesetzt wurden dann ist dann muss es einen Monat spaeter sein.
                {
                    cal_change = calSetMonthAndYear(cal_change, iTodayMonth)
                }
            } else
            // wenn monatlich eingestellt ist
            {
                if (iTodayDayOfMonth >= iChangeDayOfMonth)
                // dann kann der Tag im Monat groesser oder gleich sein
                {
                    cal_change = calSetMonthAndYear(cal_change, iTodayMonth)
                }
            }
            val iChangeYear = cal_change.get(Calendar.YEAR)
            // int iChangeMonth = cal_change.get(Calendar.MONTH);   				// Das ist der heutige Monat 1 - 12
            val iChangeDayOfYear =
                cal_change.get(Calendar.DAY_OF_YEAR)        // Heute als Tag im Jahre 1 - 36x
            val changeDate = cal_change.time                            // Heute als Datum
            val cal_begin = cal_change.clone() as Calendar
            val iMobileCycleOptions = NG_Application.applicationContext().getResources().getIntArray(R.array.MobileCycleOptionArray_Values)
            if (iMobileCycleTyp > 0)
            // wenn 30 oder 28 Tage eingestellt ist
            {
                cal_begin.roll(Calendar.DAY_OF_YEAR,iMobileCycleOptions[iMobileCycleTyp] * -1
                )
            } else
            // wenn monatlich eingestellt ist
            {
                cal_begin.roll(Calendar.MONTH, -1)

            }
            if (cal_begin.get(Calendar.MONTH) > cal_change.get(Calendar.MONTH)) {
                cal_begin.roll(Calendar.YEAR, -1)
            }
            cal_begin.set(Calendar.HOUR, 0)
            cal_begin.set(Calendar.MINUTE, 0)
            val beginDate = cal_begin.time
            val iBeginYear = cal_begin.get(Calendar.YEAR)
            val iBeginDayOfYear = cal_begin.get(Calendar.DAY_OF_YEAR)
            if (iBeginYear != iChangeYear) {
                iCorrectionDays = iDaysOfYear(context, cal_begin.get(Calendar.YEAR), 12, 31)
            }

            NG_Utils.NG_Val_Time_Cots.DateOfChange(changeDate)
            NG_Utils.NG_Val_Time_Cots.iCycleDayOne(iBeginDayOfYear)     // Anfangstag des Zyklus bzg. auf Jahr
            NG_Utils.NG_Val_Time_Cots.iCycleLength(iChangeDayOfYear + iCorrectionDays - iBeginDayOfYear) // Zykluslaenge entw. 30T oder Tage entsprchend der Monate
            if (iTodayDayOfYear >= iBeginDayOfYear) {
                NG_Utils.NG_Val_Time_Cots.iCycleDone(iTodayDayOfYear - NG_Utils.NG_Val_Time_Cots.iCycleDayOne() + 1 )       // Abgelaufene Tage im aktuellen Zyklus
            } else {
                NG_Utils.NG_Val_Time_Cots.iCycleDone(iTodayDayOfYear + iCorrectionDays - NG_Utils.NG_Val_Time_Cots.iCycleDayOne() + 1 )       // Correectiondays nur verwenden, wenn Anfang des Zyklus und Ende des Zyklus in 2 verschiedenen Jahren ist
            }
            NG_Utils.NG_Val_Time_Cots.DateOfBegin(beginDate)
            if (NG_Utils.NG_Val_Time_Cots.iCycleDone() > NG_Utils.NG_Val_Time_Cots.iCycleLength())
            // Das ist nur ein Workaround
            {                                                                                       // wenn das Wechseldatum später als die eingestellte Zykluslänge ist.
                NG_Utils.NG_Val_Time_Cots.iCycleDone(0 )                                     // Hier müsste eigentlich der Numberpicker das nicht zulassen
            }

        } catch (e: Exception) {
            Toast.makeText(context,"CalcMonitoringConstraints: Something wrong", Toast.LENGTH_SHORT).show()
            bAllesOk = false
        }

        return bAllesOk!!
    }
    fun IsADayWithinDaysRange(iDay2Consider: Int?, iRange: Int?): Boolean {
        // Log.d("IsADayWithin30Days","Start -------------------------");
        val myTempCal = Calendar.getInstance()                        // Wir lassen uns vom System ein Kalender-Objekt geben
        val iTodayYear     = myTempCal.get(Calendar.DAY_OF_YEAR)           // Das ist heute bezogen aufs Jahr
        var bResult            = false                                         // Ergebnis falsch als Default
        var iDay2ConsiderYear  = 0                                             // Das ist der Wechseltag bezogen aufs Jahr Default = 0
        val iTodayMonth    = myTempCal.get(Calendar.DAY_OF_MONTH)         // Das ist heute bezogen auf Monat
        if (iTodayMonth > iDay2Consider!!)
        // wenn heute groesser ist als der fragliche Tag dann ist es der nächste Monat
        {
            if (myTempCal.get(Calendar.MONTH) == Calendar.DECEMBER)
            // wenn diesen Monat = Dezember dann müssen wir den Jahreswechsel berücksichtigen
            {                                                                    // erst Gesamttage des Jahres bestimmen
                // dann das Wechseldatum dazurechnen
                iDay2ConsiderYear = iDaysOfYear(NG_Application.applicationContext(),myTempCal.get(Calendar.YEAR),12,31 ) + iDay2Consider!!
            }
        } else {
            myTempCal.set(Calendar.DAY_OF_MONTH, iDay2Consider!!)
            iDay2ConsiderYear = myTempCal.get(Calendar.DAY_OF_YEAR)
        }
        if (iDay2ConsiderYear - iTodayYear < iRange!!)
        // jetzt nur checken ob die Differenz der Jahrestage innerhalb des Ranges sind
        {
            bResult = true                                                        // wenn ja dann Egebnis wahr
        }
        return bResult
    }

        private fun iDaysOfYear(context: Context, iYear: Int, iMonth: Int, iDayOfMonth: Int): Int {
            var iResult = 0
            try {
                val myCal = GregorianCalendar()
                myCal.set(iYear, iMonth - 1, iDayOfMonth)
                //			Log.d("iDaysOfYear", String.valueOf(iYear)+":"+String.valueOf(iMonth)+":"+String.valueOf(iDayOfMonth));
                iResult = myCal.get(Calendar.DAY_OF_YEAR)
                //			Log.d("iDaysOfYear", String.valueOf(iResult));
            } catch (e: Exception) {
                Toast.makeText(context, "iDaysOfYear: Something wrong", Toast.LENGTH_SHORT).show()
            }
            return iResult
        }
        private fun CheckDataCalculation(WhatToCheck: Int): Boolean {
            var Result = false
            val sMobileOptions = NG_Application.applicationContext().getResources().getStringArray(R.array.mobileOptionsArray_Values)                // Hole Definitionen um zu checken
            if (NG_Utils.NG_Pref.getString(R.string.pref_MobileOptions_Key, R.string.pref_MobileOptions_Default ).equals(sMobileOptions[WhatToCheck])) {
                Result = true
            }
            return Result
        }

        fun CalculationWithoutCorrection(): Boolean {
            val Result: Boolean
            Result = CheckDataCalculation(0)
            return Result
        }

        fun CalculationWithReferenceValue(): Boolean {
            val Result: Boolean
            Result = CheckDataCalculation(1)
            return Result
        }

        fun CalculationWithBillingPeriod(): Boolean {
            val Result: Boolean
            Result = CheckDataCalculation(2)
            return Result
        }

    private fun dConvertSec2MinAndRound(dSeconds: Double): Double {        // Sekunden umrechnen in Minuten und auf eine Nachkommastelle runden
        return Math.rint(dSeconds / 60 * 10.0) / 10.0
    }

    private fun calSetMonthAndYear(calOfChange: Calendar, iTodayMonth: Int): Calendar {
        calOfChange.roll(Calendar.MONTH,1 )                                              // Kalender um einen Monat nach vorne drehen um Enddatum fuer Zyklus zu bekommen
        if (calOfChange.get(Calendar.MONTH) < iTodayMonth)
        // falls neuer Monat kleiner als aktueller Monat dann
        {
            calOfChange.roll(Calendar.YEAR,1)                                            // Jahr auch um 1 nach vorne drehen
        }
        return calOfChange
    }

    private fun CallDurationCheck(sDurSec: String): Int {
        var iCallDurationCalc = sDurSec.toInt()
        // Log.d("iCallDurationCalc", String.valueOf(iCallDurationCalc));
        // Log.d("iCallDurationCalc/60", String.valueOf((float)iCallDurationCalc/60));
        // Log.d("float iCallDurationCalc", String.valueOf(Math.floor((float)iCallDurationCalc/60)));
        try {
            if (iCallDurationCalc.toFloat() / 60 > Math.floor((iCallDurationCalc.toFloat() / 60).toDouble())) {
                iCallDurationCalc = (iCallDurationCalc.toFloat() / 60 + 1).toInt() * 60
            }
        } catch (e: Exception) {
            iCallDurationCalc = -1
        }

        // Log.d("iCallDurationCalc danach", String.valueOf((float)iCallDurationCalc));
        return iCallDurationCalc

    }


}