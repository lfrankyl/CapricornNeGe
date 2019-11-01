package de.franky.l.capricornng

import de.franky.l.capricornng.NG_Utils.NG_Pref
import java.util.Date

class NG_Values_Time_Constraints internal constructor() {
    private var iDataCalulation: Int =          0                                   // Flog ohne, Referenz oder Abrechnungszeitraum
    private var iMobileCycleOption: Int =       0                                   // Flag ob monatlicher, 30Tage oder 28Tage Zyklus 0=monatlich, 1=30 Tage, 2=28 Tage
    private var iNuPiCycle: Int =               1                                   // Wert der für das Wechseldatum eingestellt wird (Tag des Monats 1-31)
    private var iCycleDayOne: Int =             0                                   // Tag 1 des Zyklus bezogen auf Jahr
    private var iCycleLength: Int =             0                                   // Laenge des Zyklus entweder 30 Tage oder Laenge des entsprechenden Monats
    private var iCycleDone: Int =               0                                   // Bereits abgelaufene Tage im aktuelle Zyklus
    private var DateOfChange: Date? =           null                                // Tag an dem ein neuer Abrechnungszeitraum beginnt
    private var DateOfBegin: Date? =            null                                // Tag an dem der aktuelle Abrechnungszeitraum began

    private var bMobileMonitoring: Boolean =    false                               // Flag ob Monitoring mobile Daten ausgewaehlt wurde
    private var bMobileMonitoringOk: Boolean =  false                               // Flag ob Datennutzung im Rahmen ist Anzeige ob gruen oder rot
    private var bMobileViewTyp: Boolean =       false                               // Ansicht verbrauchte (0) Daten oder noch uebrige (1) Daten

    private var bCallsMonitoring: Boolean =     false                               // Flag ob Monitoring Anrufe ausgewaehlt wurde
    private var bCallsMonitoringOk: Boolean =   false                               // Flag ob Anrufe im Rahmen ist Anzeige ob gruen oder rot
    private var bCallsViewTyp: Boolean =        false                               // Ansicht verbrauchter (0) Anrufe oder noch uebrige (1) Anrufe
    private var bCallsIntegrateSMS: Boolean =   false                               // Sollen SMS mit in die Freiminuten mit eingerechnet werden oder nicht

    private var bMobResetIsLocked =             false                               // Flag zu Verriegelung des Resets der mobilen Daten, wenn Zyklus vorbei ist. Sonst passiert das immer so lange der Tag nicht vorbei ist.
    private val myContext = NG_Application.applicationContext()

    init {    }

    internal fun iDataCalulation(iValue: Int)       { NG_Pref.putString(myContext.getString(R.string.pref_MobileOptions_Value_Key), iValue.toString()) }
    internal fun iDataCalulation(): Int             { return NG_Pref.getString(R.string.pref_MobileOptions_Value_Key,R.string.pref_MobileCycleOption_Default)!!.toInt() }

    internal fun iMobileCycleOption(iValue: Int)    { NG_Pref.putString(myContext.getString(R.string.pref_MobileCycleOption_Key), iValue.toString()) }
    internal fun iMobileCycleOption(): Int          { return NG_Pref.getString(R.string.pref_MobileCycleOption_Key,R.string.pref_MobileCycleOption_Default)!!.toInt() }

    internal fun iNuPiCycle(iValue: Int)            { NG_Pref.putInt(myContext.getString(R.string.pref_NuPiCycle_Key), iValue) }
    internal fun iNuPiCycle(): Int                  { return NG_Pref.getInt_R_ID(R.string.pref_NuPiCycle_Key,R.string.pref_MobileCycle_Default) }

    internal fun iCycleDayOne(iValue: Int)          { iCycleDayOne = iValue         }
    internal fun iCycleDayOne(): Int                { return iCycleDayOne           }

    internal fun iCycleLength(iValue: Int)          { iCycleLength = iValue         }
    internal fun iCycleLength(): Int                { return iCycleLength           }

    internal fun iCycleDone(iValue: Int)            { iCycleDone = iValue           }
    internal fun iCycleDone(): Int                  { return iCycleDone             }

    internal fun DateOfChange(dValue: Date?)        { DateOfChange = dValue         }
    internal fun DateOfChange(): Date?              { return DateOfChange           }

    internal fun DateOfBegin(dValue: Date?)         { DateOfBegin = dValue          }
    internal fun DateOfBegin(): Date?               { return DateOfBegin            }

    internal fun bMobileMonitoring(bValue: Boolean) { bMobileMonitoring = bValue    }
    internal fun bMobileMonitoring(): Boolean       { return bMobileMonitoring      }

    internal fun bMobileMonitoringOk(bValue:Boolean){ bMobileMonitoringOk = bValue  }
    internal fun bMobileMonitoringOk(): Boolean     { return bMobileMonitoringOk    }

    internal fun bMobileViewTyp(bValue: Boolean)    { bMobileViewTyp = bValue       }
    internal fun bMobileViewTyp(): Boolean          { return bMobileViewTyp         }

    internal fun bCallsMonitoring(bValue: Boolean)  { bCallsMonitoring = bValue     }
    internal fun bCallsMonitoring(): Boolean        { return bCallsMonitoring       }

    internal fun bCallsMonitoringOk(bValue: Boolean){ bCallsMonitoringOk = bValue   }
    internal fun bCallsMonitoringOk(): Boolean      { return bCallsMonitoringOk     }

    internal fun bCallsViewTyp(bValue: Boolean)     { bCallsViewTyp = bValue        }
    internal fun bCallsViewTyp(): Boolean           { return bCallsViewTyp          }

    internal fun bCallsIntegrateSMS(bValue: Boolean){ bCallsIntegrateSMS = bValue   }
    internal fun bCallsIntegrateSMS(): Boolean      { return bCallsIntegrateSMS     }

    // ---------------------------------------------------------------------------------------------
    internal fun bMobResetIsLocked(bValue: Boolean){ bMobResetIsLocked = bValue   }
    internal fun bMobResetIsLocked(): Boolean      { return bMobResetIsLocked     }
}
