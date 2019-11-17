package de.franky.l.capricornng

import android.content.Context
import android.widget.Toast
import java.util.*

object NG_Utils_Cost_Values{

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
                iDay2ConsiderYear = iDaysOfYear(
                    NG_Application.applicationContext(),
                    myTempCal.get(Calendar.YEAR),
                    12,
                    31
                ) + iDay2Consider!!
            }
        } else {
            myTempCal.set(Calendar.DAY_OF_MONTH, iDay2Consider!!)
            iDay2ConsiderYear = myTempCal.get(Calendar.DAY_OF_YEAR)
        }
        if (iDay2ConsiderYear - iTodayYear < iRange!!)
        // jetzt nur checken ob die Differenz der Jahrestage innerhalb des Ranges sind
        {
            bResult =
                true                                                        // wenn ja dann Egebnis wahr
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

    }