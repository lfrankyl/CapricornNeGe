package de.franky.l.capricornng

import java.util.ArrayList
import java.util.Date

class NG_Values_Mobile_Data internal constructor() {
    private var lNuPiMobMaxVal : Long   = 0                                         // Wert des Numberpicker für den Maximalwert mobilen Daten pro Abrechnungszeitraum
    private var lNuPiMobRevVal : Long   = 0                                         // Wert des Numberpicker für den Startwert der verbrauchten mobilen Daten
    private var lMobileOffsetMessung : Long   = 0                                   // Korrekturwert wenn beim Setting der Wert anders ist als der Settingswert für Referenzwert
    private var dMobileDayMax  : Double = 0.toDouble()                              // Wert der maximal uebertragenden mobilen Daten pro Tag
    private var dMobileDayCur  : Double = 0.toDouble()                              // Aktueller Durchschnitt der uebertragenen mobilen Daten pro Tag
    private var dMobileTodayMax: Double = 0.toDouble()                              // Wert der bis heute verbraucht haette werden koennen
    private var dMobileTodayCur: Double = 0.toDouble()                              // Wert der bis heute tatsächlich verbrauchten Daten
    private val myContext = NG_Application.applicationContext()


    init {
            }

    internal fun lNuPiMobRevVal(lValue: Long)            { NG_Utils.NG_Pref.putLong(myContext.getString(R.string.pref_NuPiRef_Mob_Key), lValue) }
    internal fun lNuPiMobRevVal(): Long                  { return NG_Utils.NG_Pref.getLong(R.string.pref_NuPiRef_Mob_Key,R.string.pref_MobileRefVal_Default) }

    internal fun lNuPiMobMaxVal(lValue: Long)            { NG_Utils.NG_Pref.putLong(myContext.getString(R.string.pref_NuPiMax_Mob_Key), lValue) }
    internal fun lNuPiMobMaxVal(): Long                  { return NG_Utils.NG_Pref.getLong(R.string.pref_NuPiMax_Mob_Key,R.string.pref_MobileMaxVal_Default) }

    internal fun lMobileOffsetMessung(lValue: Long)      { NG_Utils.NG_Pref.putLong(myContext.getString(R.string.pref_MobileMessung_Key), lValue) }
    internal fun lMobileOffsetMessung(): Long            { return NG_Utils.NG_Pref.getLong(R.string.pref_MobileMessung_Key,R.string.pref_MobileRefVal_Default) }

    internal fun dMobileDayMax(dValue: Double)          { dMobileDayMax = dValue         }
    internal fun dMobileDayMax(): Double                { return dMobileDayMax           }

    internal fun dMobileDayCur(dValue: Double)          { dMobileDayCur = dValue         }
    internal fun dMobileDayCur(): Double                { return dMobileDayCur           }

    internal fun dMobileTodayMax(dValue: Double)          { dMobileTodayMax = dValue     }
    internal fun dMobileTodayMax(): Double                { return dMobileTodayMax       }

    internal fun dMobileTodayCur(dValue: Double)          { dMobileTodayCur = dValue     }
    internal fun dMobileTodayCur(): Double                { return dMobileTodayCur       }

}
