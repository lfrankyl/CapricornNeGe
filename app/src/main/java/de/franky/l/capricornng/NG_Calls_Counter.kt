package de.franky.l.capricornng

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import android.content.BroadcastReceiver
import android.telephony.TelephonyManager
import android.telephony.PhoneStateListener
import android.util.Log;






class CallService : Service() {

    var startTime: Long = 0
    var endTime: Long = 0
    var totalTime: Long = 0

    override fun onBind(intent: Intent): IBinder? {
        // TODO Auto-generated method stub
        return null
    }

    override fun onDestroy() {

        Toast.makeText(this, "Call Service stopped", Toast.LENGTH_LONG).show()

        endTime = System.currentTimeMillis() / 1000
        Toast.makeText(this, "endTime = $endTime", Toast.LENGTH_LONG).show()
        totalTime = endTime - startTime
        Toast.makeText(this, "totalTime = $totalTime", Toast.LENGTH_LONG).show()

    }

    override fun onStart(intent: Intent, startid: Int) {
        Toast.makeText(this, "Call Service started by user.", Toast.LENGTH_LONG).show()

        startTime = System.currentTimeMillis() / 1000
        Toast.makeText(this, "startTime = $startTime", Toast.LENGTH_LONG).show()
    }


}
class OutgoingCallReciever : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        context.startService(Intent(context, CallService::class.java))
    }

}

private  class ListenToPhoneStateII : PhoneStateListener() {

    internal var callEnded = false
    override fun onCallStateChanged(state: Int, incomingNumber: String) {

        when (state) {
            TelephonyManager.CALL_STATE_IDLE -> {
                Log.d("State changed: ", state.toString() + "Idle")


                if (callEnded) {
                    //you will be here at **STEP 4**
                    //you should stop service again over here
                } else {
                    //you will be here at **STEP 1**
                    //stop your service over here,
                    //i.e. stopService (new Intent(`your_context`,`CallService.class`));
                    //NOTE: `your_context` with appropriate context and `CallService.class` with appropriate class name of your service which you want to stop.

                }
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                Log.d("State changed: ", state.toString() + "Offhook")
                //you will be here at **STEP 3**
                // you will be here when you cut call
                callEnded = true
            }
            TelephonyManager.CALL_STATE_RINGING -> Log.d("State changed: ", state.toString() + "Ringing")


            else -> {
            }
        }//you will be here at **STEP 2**
    }

}