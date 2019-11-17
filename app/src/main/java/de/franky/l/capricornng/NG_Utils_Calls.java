package de.franky.l.capricornng;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;

import androidx.legacy.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class NG_Utils_Calls
{


	private static int iCalc_SMS(Context context)
	{
		int iNoSMS = 0;

		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {

			GregorianCalendar myCal = new GregorianCalendar();
			myCal.setTime(NG_Utils.CurVal.DateOfBegin);
			myCal.set(Calendar.AM_PM, Calendar.AM);
			myCal.set(Calendar.HOUR, 0);
			myCal.set(Calendar.MINUTE, 0);
			myCal.set(Calendar.SECOND, 0);
			// Log.d("myCal.getTime", String.valueOf(myCal.getTime()));
			try {

				final String[] projection = null;
				final String selection = null;
				final String[] selectionArgs = null;
				final String sortOrder = "DATE DESC";
				final Cursor Cpc_SMS_Cursor = context.getContentResolver().query(Uri.parse("content://sms/sent"), projection, selection, selectionArgs, sortOrder);
				if (Cpc_SMS_Cursor != null) {

					int type = Cpc_SMS_Cursor.getColumnIndex(CallLog.Calls.TYPE);
					int date = Cpc_SMS_Cursor.getColumnIndex(CallLog.Calls.DATE);
					NG_Utils.CurVal.bCallsMonitoring = NG_Pref.getBool(R.string.pref_CallsMonitoring_Key, false);
					if (Cpc_SMS_Cursor.moveToFirst())
					{
						// Loop through the call log.
						do
						{
							String callDate = Cpc_SMS_Cursor.getString(date);
							Date callDayTime = new Date(Long.valueOf(callDate));
							if ((!callDayTime.before(myCal.getTime())) || !NG_Utils.CurVal.bCallsMonitoring || !NG_Utils.CurVal.bMobileDataCycle) {
								switch (Cpc_SMS_Cursor.getInt(type)) {
									case CallLog.Calls.OUTGOING_TYPE:
										iNoSMS = iNoSMS +1;
										// Log.d("outgoing zaehlt",String.valueOf(iCalls__Out_Num) + " " + callDayTime + " " + String.valueOf(iCallDurationCalc));
										break;
									case CallLog.Calls.INCOMING_TYPE:
										// Log.d("incoming",String.valueOf(iCalls___In_Num) + " " + callDayTime + " " + iCallDurationCalc);
										break;
								}
							}
						} while (Cpc_SMS_Cursor.moveToNext());

					}
					Cpc_SMS_Cursor.close();
				}

			} catch (Exception e) {
				Log.e("bCalc_SMS", "Exception");
				//e.printStackTrace();
				iNoSMS  = 0;
			}
		}
		return iNoSMS;
	}

	private static boolean bCalc_Calls(Context context)
	{
		boolean bAllesOk = true;
		int iCalls__Out_Min = 0;
		int iCalls__Out_Num = 0;
		int iCalls___In_Min = 0;
		int iCalls___In_Num = 0;
		int iCalls_Miss_Num = 0;
		double dCallsMinOutgoingUsed;								// Summe der Minuten der verbrauchten abgehenden Anrufe
		double dCallsMinOutgoingLeft;								// Summe der Minuten der uebrigen abgehenden Anrufe

		GregorianCalendar myCal = new GregorianCalendar();
		myCal.setTime(NG_Utils.CurVal.DateOfBegin);
		myCal.set(Calendar.AM_PM, Calendar.AM);
		myCal.set(Calendar.HOUR, 0);
		myCal.set(Calendar.MINUTE, 0);
		myCal.set(Calendar.SECOND, 0);
		// Log.d("myCal.getTime", String.valueOf(myCal.getTime()));
		try {

			final String[] projection = null;
			final String selection = null;
			final String[] selectionArgs = null;
			final String sortOrder = "DATE DESC";
			final Cursor Cpc_Cursor = context.getContentResolver().query(Uri.parse("content://call_log/calls"), projection, selection, selectionArgs, sortOrder);
			if (Cpc_Cursor != null) {
                NG_Utils.CurVal.FreePhoneNo = NG_Pref.getStringArray(context.getString(R.string.pref_Calls_FreeNumbArr_Key));
				int type = Cpc_Cursor.getColumnIndex(CallLog.Calls.TYPE);
				int date = Cpc_Cursor.getColumnIndex(CallLog.Calls.DATE);
				int duration = Cpc_Cursor.getColumnIndex(CallLog.Calls.DURATION);
				int number = Cpc_Cursor.getColumnIndex(CallLog.Calls.NUMBER);
				NG_Utils.CurVal.bCallsMonitoring = NG_Pref.getBool(R.string.pref_CallsMonitoring_Key, false);
				if (Cpc_Cursor.moveToFirst())
				{
					// Loop through the call log.
					ArrayList<String> sLocalFreePhoneNo = new ArrayList<String>();
					for(int i=0 ; i< NG_Utils.CurVal.FreePhoneNo.size();i++){
						sLocalFreePhoneNo.add(NG_Utils.CurVal.FreePhoneNo.get(i).replaceAll(" ","").trim());
					}

					do
					{
						String callDate = Cpc_Cursor.getString(date);
						Date callDayTime = new Date(Long.valueOf(callDate));
						String callDuration = Cpc_Cursor.getString(duration);
						String callNumber = Cpc_Cursor.getString(number);
						/*
						Log.d("callDayTime", String.valueOf(callDayTime));Log.d("bCallsMonitoring", String.valueOf((! callDayTime.before(myCal.getTime()))));
						Log.d("bCallsMonitoring", String.valueOf( NG_Utils.CurVal.bCallsMonitoring == true) );
						Log.d("bCallsMonitoring", String.valueOf( NG_Utils.CurVal.bMobileDataCycle == true));
						*/
						int iCallDurationCalc = CallDurationCheck(callDuration);
						// Log.d("Cursor auswerten",String.valueOf((!callDayTime.before(myCal.getTime())) || !NG_Utils.CurVal.bCallsMonitoring || !NG_Utils.CurVal.bMobileDataCycle));
						if ((!callDayTime.before(myCal.getTime())) || !NG_Utils.CurVal.bCallsMonitoring || !NG_Utils.CurVal.bMobileDataCycle) {
							switch (Cpc_Cursor.getInt(type)) {
								case CallLog.Calls.OUTGOING_TYPE:
                                    if (! sLocalFreePhoneNo.contains(callNumber)) {
                                        iCalls__Out_Num = iCalls__Out_Num + 1;
                                        iCalls__Out_Min = iCalls__Out_Min + iCallDurationCalc;
//                                        Log.d("outgoing zaehlt", String.valueOf(iCalls__Out_Num) + " " + callNumber + " " + callDayTime + " " + String.valueOf(iCallDurationCalc));
//                                    }
//                                    else
//                                    {
//                                        Log.d("outgoing zaehlt nicht", String.valueOf(iCalls__Out_Num) + " " + callNumber + " " + callDayTime + " " + String.valueOf(iCallDurationCalc));
                                    }
									break;
								case CallLog.Calls.INCOMING_TYPE:
									iCalls___In_Num = iCalls___In_Num + 1;
									iCalls___In_Min = iCalls___In_Min + iCallDurationCalc;
									// Log.d("incoming",String.valueOf(iCalls___In_Num) + " " + callDayTime + " " + iCallDurationCalc);
									break;
								case CallLog.Calls.MISSED_TYPE:
									iCalls_Miss_Num = iCalls_Miss_Num + 1;
									// Log.d("missed",String.valueOf(iCalls_Miss_Num) + " " + callDayTime + " " + iCallDurationCalc);
									break;
							}
						}
						//else
						//{
						//  Log.d("outgoing zaehlt nicht",String.valueOf(iCalls___In_Num) + " " + callDayTime + " " + iCallDurationCalc);
						//}
					} while (Cpc_Cursor.moveToNext());

				}
			Cpc_Cursor.close();
			}

			double dCallsMaxVal = (double) NG_Pref.getLong(R.string.pref_NuPiMax_Calls_Key, R.string.pref_Calls_MaxVal_Default);
			long lCallsRefVal = NG_Pref.getLong(R.string.pref_NuPiRef_Calls_Key, R.string.pref_Calls_RefVal_Default);
			// Hier ist kodiert wenn keine Anpassung verlangt wird.
			if (CalculationWithoutCorrection()) {
				lCallsRefVal = 0;
			}

			dCallsMinOutgoingUsed = dConvertSec2MinAndRound((double) (iCalls__Out_Min + lCallsRefVal *60 )); // Sekunden umrechnen in Minuten und auf eine Nachkommastelle runden
			if (NG_Utils.CurVal.bCallsIntegrateSMS)
			{
				NG_Utils.CurVal.iSMSNumSent = iCalc_SMS(context);
				dCallsMinOutgoingUsed = dCallsMinOutgoingUsed + NG_Utils.CurVal.iSMSNumSent;  // wenn der Vertrag Minuten und SMS kombiniert, dann diese zu den verbrauchten Minuten dazuzählen
			}

			NG_Utils.CurVal.dCallsMinIncoming = dConvertSec2MinAndRound((double) iCalls___In_Min);		// Sekunden umrechnen in Minuten und auf eine Stelle nach dem Komma runden

			NG_Utils.CurVal.iCallsNumIncoming = iCalls___In_Num;
			NG_Utils.CurVal.iCallsNumOutgoing = iCalls__Out_Num;
			NG_Utils.CurVal.iCallsNumMissed = iCalls_Miss_Num;

			NG_Utils.CurVal.dCallsMinOutgoingDayMax = dCallsMaxVal / NG_Utils.CurVal.iCycleLength;                      // Erlaubte Anrufe / Tag
			NG_Utils.CurVal.dCallsMinOutgoingDayCur = dCallsMinOutgoingUsed / NG_Utils.CurVal.iCycleDone; // Aktuell ausgehende Anrufe / Tag
			if (lCallsRefVal > 0) {
				NG_Utils.CurVal.iCallsNumOutgoing = NG_Utils.CurVal.iCallsNumOutgoing + 1; // Wenn Referenzwert angegeben dann ein Anruf addieren
			}
			NG_Utils.CurVal.dCallsMinOutgoingTodayMax = dCallsMaxVal / NG_Utils.CurVal.iCycleLength * NG_Utils.CurVal.iCycleDone;      // Max. moegliche Anrufe bis heute wenn linear

			dCallsMinOutgoingLeft = dCallsMaxVal - dCallsMinOutgoingUsed;
			if (dCallsMinOutgoingLeft > 0)  // uebrigen Freiminuten immer negativ anzeigen
			{
				dCallsMinOutgoingLeft = dCallsMinOutgoingLeft * -1;
			}
			NG_Utils.CurVal.bCallsMonitoringOk = dCallsMinOutgoingUsed / NG_Utils.CurVal.iCycleDone <= dCallsMaxVal / NG_Utils.CurVal.iCycleLength;
			NG_Utils.CurVal.bCallsViewTyp = NG_Pref.getBool(R.string.pref_Calls_View_Key, false);

			if (NG_Utils.CurVal.bCallsViewTyp && NG_Utils.CurVal.bCallsMonitoring) {
				NG_Utils.FillDispDataArray(8,dCallsMinOutgoingLeft, String.valueOf(dCallsMinOutgoingLeft),"Min");
			} else {
				NG_Utils.FillDispDataArray(8,dCallsMinOutgoingLeft, String.valueOf(dCallsMinOutgoingUsed),"Min");
			}

			if (NG_Utils.CurVal.bCallsMonitoring && NG_Utils.CurVal.bMobileDataCycle)
			{
				if (NG_Utils.CurVal.bCallsMonitoringOk)
				{
					NG_Utils.CurVal.DispData[8].setIcon(R.drawable.cpc_phone_green);
				}
				else
				{
					NG_Utils.CurVal.DispData[8].setIcon(R.drawable.cpc_phone_red);
				}
			}
			else
			{
				NG_Utils.CurVal.DispData[8].setIcon(R.drawable.cpc_phone_gen);
			}

		/*
		Log.d("bCalc_Calls iCycleDone",Integer.toString(NG_Utils.CurVal.iCycleDone));
		Log.d("bCalc_Calls iCycleLength",Integer.toString(NG_Utils.CurVal.iCycleLength));
		Log.d("bCalc_Calls dCallsMax",String.valueOf(dCallsMaxVal));
		Log.d("bCalc_Calls CurVal.dCallsMinOutgoingUsed",String.valueOf(NG_Utils.CurVal.dCallsMinOutgoingUsed));
		Log.d("bCalc_Calls CurVal.dCallsMinOutgoingLeft",String.valueOf(NG_Utils.CurVal.dCallsMinOutgoingLeft));
		Log.d("bCalc_Calls CurVal.dCallsMinOutgoingDayMax",String.valueOf(NG_Utils.CurVal.dCallsMinOutgoingDayMax));
		Log.d("bCalc_Calls dCallsMinOutgoingDayCur",String.valueOf(NG_Utils.CurVal.dCallsMinOutgoingDayCur));
		Log.d("bCalc_Calls dCallsMinOutgoingTodayMax",String.valueOf(NG_Utils.CurVal.dCallsMinOutgoingTodayMax));
		Log.d("bCalc_Calls iCycleDayOne",Integer.toString(NG_Utils.CurVal.iCycleDayOne));
		Log.d("bCalc_Calls bCallsViewTyp", Boolean.toString(NG_Utils.CurVal.bCallsViewTyp));
		*/
		} catch (Exception e) {
			Log.e("bCalc_Calls", "Exception");
			//e.printStackTrace();
			bAllesOk = false;
		}
	return bAllesOk;
	}




	
} 	// End of class NG_Utils_Calls

