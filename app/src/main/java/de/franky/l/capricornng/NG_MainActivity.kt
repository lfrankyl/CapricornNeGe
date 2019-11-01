package de.franky.l.capricornng

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import java.util.ArrayList

class NG_MainActivity : AppCompatActivity() {

    private var btnStd: ToggleButton? = null
    private var btnMobile: ToggleButton? = null
    private var btnWifi: ToggleButton? = null
    private var btnCalls: ToggleButton? = null
    private var btnNetwrk: ToggleButton? = null

    private val SETTINGS_RESULT = 1
    private val HELP_RESULT = 2
    private val INFO_RESULT = 3

    private var NG_frag: MutableList<androidx.fragment.app.Fragment>? = null
    private var NG_pager: androidx.viewpager.widget.ViewPager? = null

    private var HSV: HorizontalScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ng_main)

        NG_frag = getFragments

        val pageAdapter = Cpc_PageAdapter(supportFragmentManager, NG_frag!!)
        NG_pager = findViewById<androidx.viewpager.widget.ViewPager>(R.id.ng_viewpager_main)
        NG_pager!!.adapter =pageAdapter

        NG_pager!!.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                val display = windowManager.defaultDisplay
                val outMetrics = DisplayMetrics()
                display.getMetrics(outMetrics)

                val dpWidth = outMetrics.widthPixels
                val ll: LinearLayout = findViewById(R.id.ll_main_button);
                val iHorzScroll = ((ll.width - dpWidth) / 2);
                val hsv: HorizontalScrollView? = findViewById(R.id.hsv_btn_main);
                try {
                    if(BuildConfig.WithCalls) {

                        when (position) {
                            4 -> {
                                setButtonCheckStatus(false, false, false, true, false)
                                hsv?.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                            }
                            3 -> {
                                setButtonCheckStatus(false, false, true, false, false)
                                hsv?.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                            }
                            2 -> {
                                setButtonCheckStatus(false, false, false, false, true)
                                hsv?.post({hsv.smoothScrollTo(iHorzScroll, 0)})
                            }
                            1 -> {
                                setButtonCheckStatus(false, true, false, false, false)
                                hsv?.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                            }
                            else                            // includes 0 (Standard)
                            -> {
                                setButtonCheckStatus(true, false, false, false, false)
                                hsv?.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                               }
                        }
                    }
                    else
                    {
                        when (position) {
                            3 -> {
                                setButtonCheckStatus(false, false, false, true, false)
                                hsv?.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                            }
                            2 -> {
                                setButtonCheckStatus(false, false, true, false, false)
                                hsv?.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                            }
                            1 -> {
                                setButtonCheckStatus(false, true, false, false, false)
                                hsv?.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                            }
                            else                            // includes 0 (Standard)
                            -> {
                                setButtonCheckStatus(true, false, false, false, false)
                                hsv?.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                            }
                        }
                    }

                } catch (e: Exception) {
                    //                    Toast.makeText(getApplicationContext(), "Cpc_Main btn not exists", Toast.LENGTH_SHORT).show();
                    Log.e("NG_Main", "btn not exists")
                }

            }
        })

        val tManager: TelephonyManager? = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager;
        val listener:ListenToPhoneState? = ListenToPhoneState();
        tManager?.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private fun setButtonCheckStatus(Std: Boolean?,Mobile: Boolean?,Wifi: Boolean?,Netwrk: Boolean?,Calls: Boolean?) {
        btnStd?.setChecked(Std!!)
        btnMobile?.setChecked(Mobile!!)
        btnWifi?.setChecked(Wifi!!)
        btnNetwrk?.setChecked(Netwrk!!)
        if(BuildConfig.WithCalls){
            btnCalls?.setChecked(Calls!!)
        }
    };

    private
    val getFragments: MutableList<androidx.fragment.app.Fragment>
        get() {
            val fList = ArrayList<androidx.fragment.app.Fragment>()

            fList.add(NG_Frag_Std.newInstance("Fragment 1", "std"))
            fList.add(NG_Frag_Mobile.newInstance("Fragment 1", "mobile"))
            if(BuildConfig.WithCalls) {
                fList.add(NG_Frag_Calls.newInstance("Fragment 1", "calls"));
            }
            fList.add(NG_Frag_Wlan.newInstance("Fragment 1", "wlan"))
            fList.add(NG_Frag_Netwrk.newInstance("Fragment 1", "netwrk"))
            return fList
        }



    private inner class Cpc_PageAdapter(fm: androidx.fragment.app.FragmentManager, private val cpc_fragments: List<androidx.fragment.app.Fragment>) :
        androidx.fragment.app.FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): androidx.fragment.app.Fragment {
            return this.cpc_fragments[position]
        }

        override fun getCount(): Int {
            return this.cpc_fragments.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val tFr = super.instantiateItem(container, position) as androidx.fragment.app.Fragment
            NG_frag?.set(position, tFr)
            return tFr
        }
    }
    fun Boolean.toInt() = if (this) 1 else 0

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.setting_menu, menu);


        btnNetwrk = findViewById<ToggleButton>(R.id.btnNetwrk)
        btnNetwrk!!.setOnClickListener(View.OnClickListener
        {
            NG_pager!!.setCurrentItem(3+(BuildConfig.WithCalls).toInt())
            setButtonCheckStatus(false, false, false, true, false)
        })

        btnWifi = findViewById<ToggleButton>(R.id.btnWifi)
        btnWifi!!.setOnClickListener(View.OnClickListener
        {
            NG_pager!!.setCurrentItem(2+(BuildConfig.WithCalls).toInt())
            setButtonCheckStatus(false, false, true, false, false)
        })


        btnCalls = findViewById(R.id.btnCalls);
        if(BuildConfig.WithCalls) {

            btnCalls!!.setOnClickListener(View.OnClickListener()
            {
                NG_pager!!.setCurrentItem(2);
                setButtonCheckStatus(false, false, false, false, true);
            });
        }
        else
        {
            btnCalls!!.visibility =View.GONE

        }
        btnMobile = findViewById<ToggleButton>(R.id.btnMobile)
        btnMobile!!.setOnClickListener(View.OnClickListener
        {
            NG_pager!!.setCurrentItem(1)
            setButtonCheckStatus(false, true, false, false, false)
        })

        btnStd = findViewById<ToggleButton>(R.id.btnStd)
        btnStd!!.setOnClickListener(View.OnClickListener
        {
            NG_pager!!.setCurrentItem(0)
            setButtonCheckStatus(true, false, false, false, false)
        })


    val btnUpdate = findViewById<Button>(R.id.btn_Update)
    btnUpdate.setOnClickListener    {
        //Cpc_Utils_Data_Val.JustGetTheValues(Cpc_Application.getContext())
        val iSelItem = NG_pager!!.getCurrentItem()
        //Log.d("btnUpdate",String.valueOf(iSelItem) );
        NG_pager!!.setCurrentItem(iSelItem)
        try {
            val mFr = NG_frag!![iSelItem]
            if (mFr != null) {
                //                        Log.d("btnUpdate","mFr Fragment is visible " + String.valueOf(mFr.isVisible()));
                if (mFr.isVisible()) {
                    mFr.onStart()
                }
            }
            //                    else
            //                    {
            //                        Log.d("btnUpdate","mFr Fragment is null");
            //                    }
        } catch (e: Exception) {
            Log.e("btnUpdate", "Fragment does not exist")
        }
    }
    return true
}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        val i : Intent;
        return when (item.itemId) {
            R.id.menu_about -> {
                i = Intent(NG_Application.applicationContext(),NG_Activity_InfoHeader::class.java)
                startActivityForResult(i, 3)
                true
            }
            R.id.menu_help -> {
                i = Intent(NG_Application.applicationContext(), NG_Activity_HelpHeader::class.java)
                startActivityForResult(i, 3)
                true
            }
            R.id.menu_setting -> {
                    i = Intent(NG_Application.applicationContext(),NG_Activity_SettingsHeader::class.java)
                    startActivityForResult(i, 3)
                    true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



}
private class ListenToPhoneState : PhoneStateListener() {

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