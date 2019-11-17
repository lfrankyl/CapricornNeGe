package de.franky.l.capricornng

import android.content.Context
import android.util.Log

import de.franky.l.capricornng.NG_Utils.NG_Pref

import java.util.Calendar


internal object NG_Utils_Data_Val {


    fun Get_Mob_Data_Values(context: Context): Boolean {

        // **********************************************************************************************
        // Make all values for mobile data
        //
        //
        //
        var lNuPi_Mobile_RefVal: Long
        var lMobileOffset: Long
        var lMobileMessung: Long
        val lwlanMessung: Long
        var dMobileUsed: Double = 574.0                             // Wert fuer verbrauchte mobile Daten
        var dMobileLeft: Double = 978.0                             // Wert fuer noch uebrige mobile Daten



        lNuPi_Mobile_RefVal =  NG_Utils.NG_Val_Mob_Data.lNuPiMobRevVal()
        if (lNuPi_Mobile_RefVal < 0) {
            lNuPi_Mobile_RefVal = lNuPi_Mobile_RefVal * -1
        }
        //Log_Mob("lNuPi_Mobile_RefVal",lNuPi_Mobile_RefVal.toDouble());
        lMobileOffset = NG_Utils.NG_Val_Mob_Data.lMobileOffsetMessung()
        //Log_Mob("lMobileOffset",lMobileOffset.toDouble());

        // Log.d("JustGetTheValues pref_WlanSetup_Key",String.valueOf(Cpc_Std_Data.getBoolean(context.getString(R.string.pref_WlanSetup_Key), false)));
        // Log.d("JustGetTheValues pref_FirstSetup_Key",String.valueOf(Cpc_Std_Data.getBoolean(context.getString(R.string.pref_FirstSetup_Key), false)));
        lMobileMessung = NG_Utils.NG_Val_Wifi.Mobile_Total()                // Mobile Daten aus System Daten holen
        //Log_Mob("lMobileMessung",lMobileMessung.toDouble());
        if (lMobileMessung == 0L || NG_Utils.NG_Val_Wifi.IsWLANandLollipop())
        // keine mobilen Daten gemessen werden z.B. wenn Lollopop und wlan aktiv oder Mobile Daten komplett aus
        {
            lMobileMessung = NG_Pref.getLong(R.string.pref_MobileMessungSaved_Key,0)      // Deshalb Wert aus Speicher holen und anzeigen
            // Log.d("WLAN and Lollipop lMobileMessung",String.valueOf(lMobileMessung));
        } else
        // wenn kein wlan oder kein Lollipop
        {
            // Die App wurde gerade frisch installiert oder jmd hat die App-Daten geloescht
            if (!NG_Pref.getBool(context.getString(R.string.pref_FirstSetup_Key), false)) {
                // Das erste Setup wurde per wlan gemacht
                if (NG_Utils.NG_Val_Wifi.bWIFISetup()) {
                    NG_Pref.putBool(context.getString(R.string.pref_FirstSetup_Key),true )                        //Flag setzen dass richtige mobile Daten gemessen wurden
                    lMobileOffset = lMobileMessung
                    NG_Utils.NG_Val_Mob_Data.lMobileOffsetMessung(lMobileMessung)
                }
            }
            NG_Pref.putLong(context.getResources().getString(R.string.pref_MobileMessungSaved_Key), lMobileMessung)            // in NG Speicher ablegen falls oben
            // Log.d("Mobile or not Lollipop lMobileMessung",String.valueOf(lMobileMessung));
        }

        lwlanMessung = NG_Utils.NG_Val_Wifi.Wifi_Total()
        //Log.d("JustGetTheValues  lwlanMessung",String.valueOf(lwlanMessung));
        //Log_Mob("lMobileMessung",lMobileMessung.toDouble());


        val sMobileCycleOptions = context.resources.getStringArray(R.array.MobileCycleOptionArray_Values)   // was bzgl. Mobile Daten eingestellt ist

        // Hier ist kodiert wenn keine Anpassung verlangt wird.
        if (NG_Utils_Data_Constraints.CalculationWithoutCorrection()) {
            dMobileUsed = lMobileMessung.toDouble()
        }

        // Hier ist kodiert wenn Referenzwert verlangt ist und sonst keine weitere Korrektur
        if (NG_Utils_Data_Constraints.CalculationWithReferenceValue()) {
            dMobileUsed = (lNuPi_Mobile_RefVal + lMobileMessung - lMobileOffset).toDouble()
        }

        // Hier muss das kodiert werden, wenn ein Zyklus verlangt ist
        if (NG_Utils_Data_Constraints.CalculationWithBillingPeriod()) {
            // Log.d("Zyklus", Cpc_Std_Data.getString(context.getString(R.string.pref_MobileCycleOption_Key), context.getString(R.string.pref_MobileCycleOption_Default)));
            val myCal = Calendar.getInstance()
            val iCurDay = myCal.get(Calendar.DAY_OF_MONTH)  // current day of month
            val iNuPi_Mobile_Cycle: Int

            iNuPi_Mobile_Cycle = NG_Pref.getInt_R_ID(R.string.pref_NuPiCycle_Key, R.string.pref_MobileCycle_Default)
            //Log.d("Heute ist der Tag",String.valueOf(iCurDay == iNuPi_Mobile_Cycle));
            //Log.d("Wir haben es schon gemacht: ",String.valueOf(NG_Utils.CurVal.bMobResetLocked()));
            if (iCurDay == iNuPi_Mobile_Cycle)                                                  // Jetzt ist der Tag an dem die mobilen Daten zurueckgesetzt werden muessen
            {
                if (!NG_Utils.NG_Val_Time_Cots.bMobResetIsLocked())                             // Falls wir das noch nicht gemacht haben tun wir das, sonst nicht.
                {
                    Log.d("I machs", iNuPi_Mobile_Cycle.toString())
                    NG_Utils.NG_Val_Time_Cots.bMobResetIsLocked(true)                   // verriegeln das das nur einmal gemacht wird
                    dMobileUsed = 0.0
                    lNuPi_Mobile_RefVal = 0
                    NG_Utils.NG_Val_Mob_Data.lMobileOffsetMessung(lMobileMessung)
                    NG_Utils.NG_Val_Mob_Data.dMobileTodayCur(dMobileUsed)

                    NG_Pref.putLong(context.getString(R.string.pref_NuPiRef_Calls_Key), 0)
                    when (NG_Utils.NG_Val_Time_Cots.iMobileCycleOption()) {
                        1 ->                                                                    // wenn 30 Tage Zyklus verlangt ist
                            myCal.roll(Calendar.DAY_OF_YEAR, 30)
                        2 ->                                                                    // wenn 28 Tage Zyklus verlangt ist
                            myCal.roll(Calendar.DAY_OF_YEAR, 28)
                        else ->                                                                 // wenn monatlicher Zyklus verlangt ist
                            myCal.roll(Calendar.MONTH, 1)
                    }
                    NG_Pref.putInt(context.getString(R.string.pref_NuPiCycle_Key),myCal.get(Calendar.DAY_OF_MONTH))
                    // Log.d("MobReset",String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy", myCal.getTime())));
                    // Log.d("lMobileMessung",String.valueOf(lMobileMessung));
                }

            } else {
                //Log.d("I machs ned",String.valueOf(iNuPi_Mobile_Cycle));
                NG_Utils.NG_Val_Time_Cots.bMobResetIsLocked(false)

            }
            dMobileUsed = (lNuPi_Mobile_RefVal + lMobileMessung - lMobileOffset).toDouble()
        }

        val bAllesOk = NG_Utils_Data_Constraints.CalcMonitoringConstraints(context)
        // Log.d("CalcMonitoringConstraints", String.valueOf(bAllesOk));

        NG_Utils.NG_Val_Time_Cots.bMobileMonitoring(NG_Pref.getBool(R.string.pref_MobileMonitoring_Key, false))
         Log.d("Monitoring ?",NG_Utils.NG_Val_Time_Cots.bMobileMonitoring().toString());
        if ( NG_Utils.NG_Val_Time_Cots.bMobileMonitoring())
        // Ueberwachung der mobilen Daten ist eingestellt
        {
            val dMobMax = NG_Pref.getLong(R.string.pref_NuPiMax_Mob_Key,R.string.pref_MobileMaxVal_Default ).toDouble()                             // Maxwert in Byte

            NG_Utils.NG_Val_Mob_Data.dMobileDayMax(dMobMax / NG_Utils.NG_Val_Time_Cots.iCycleLength())        // Erlaubte Byte / Tag
            NG_Utils.NG_Val_Mob_Data.dMobileDayCur(dMobileUsed / NG_Utils.NG_Val_Time_Cots.iCycleDone())      // Aktuell verbrauchte Byte / Tag
            NG_Utils.NG_Val_Mob_Data.dMobileTodayMax( dMobMax / NG_Utils.NG_Val_Time_Cots.iCycleLength() * NG_Utils.NG_Val_Time_Cots.iCycleDone())        // Max. moegliche Menge bis heute wenn linear
            dMobileLeft = dMobMax - dMobileUsed
            if (dMobileLeft < 0)
            // uebrige Daten erstmal immer positiv machen damit MakeOutString funktioniert
            {
                dMobileLeft = dMobileLeft * -1
            }

            NG_Utils.NG_Val_Time_Cots.bMobileMonitoringOk(dMobileUsed / NG_Utils.NG_Val_Time_Cots.iCycleDone() <= dMobMax / NG_Utils.NG_Val_Time_Cots.iCycleLength())

            /*
			Log.d("Monitoring CurVal.iCycleDone",Integer.toString(NG_Utils.NG_Val_Time_Cots.iCycleDone));
			Log.d("Monitoring CurVal.iCycleLength",Integer.toString(NG_Utils.NG_Val_Time_Cots.iCycleLength));
			Log.d("Monitoring CurVal.iCycleDayOne",Integer.toString(NG_Utils.NG_Val_Time_Cots.iCycleDayOne));
			*/

        //    Log_Mob("dMobileUsed",dMobileUsed.toDouble());
        //    Log_Mob("lMobileMessung",lMobileMessung.toDouble());
			// Log.d("Monitoring dMobMax",String.valueOf(dMobMax));
			//Log.d("Monitoring dMobMax/iCycleLength",String.valueOf(dMobMax/NG_Utils.NG_Val_Time_Cots.iCycleLength));
			//Log.d("Monitoring CurValUsed.dMobile/CurVal.iCycleDone",String.valueOf(dMobileUsed/NG_Utils.NG_Val_Time_Cots.iCycleDone));

			Log.d("Monitoring debugging","Ende -------------------------");

        }
        NG_Utils.NG_Val_Mob_Data.lMobileOffsetMessung(lMobileMessung)
        NG_Utils.NG_Val_Mob_Data.lNuPiMobRevVal(dMobileUsed.toLong())

        NG_Pref.putLong(context.getString(R.string.pref_wlanOffset_Key), lwlanMessung)

        if (NG_Utils.NG_Val_Time_Cots.bMobileViewTyp() && NG_Utils.NG_Val_Time_Cots.bCallsMonitoring())
        {
            NG_Utils.NG_Val_Mob_Data.dMobileTodayCur(dMobileLeft)
        } else
        {
            NG_Utils.NG_Val_Mob_Data.dMobileTodayCur(dMobileUsed)
        }



        return bAllesOk
    }

//    fun Log_Mobile(sTag:String,dMsg:Double ){
//        Log.d(sTag,NG_Utils.MakeOutString(dMsg) + NG_Utils.CalcUnit(dMsg));
//    }

}    // End of class NG_Utils_Data_Val

