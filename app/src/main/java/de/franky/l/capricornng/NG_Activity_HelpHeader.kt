package de.franky.l.capricornng

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ToggleButton
import java.util.ArrayList
import android.widget.LinearLayout
import android.util.DisplayMetrics




class NG_Activity_HelpHeader : AppCompatActivity() {

    private var btnGen: ToggleButton? = null
    private var btnWidget: ToggleButton? = null
    private var btnCost: ToggleButton? = null
    private var btnWifi: ToggleButton? = null
    private var btnApp: ToggleButton? = null

    private var NG_frag: MutableList<Fragment>? = null
    private var NG_pager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.ng_help_screen)
        NG_frag = getFragments

        val pageAdapter = NG_PageAdapter(supportFragmentManager, NG_frag!!)
        NG_pager = findViewById<ViewPager>(R.id.ng_viewpager_help)
        NG_pager!!.adapter =pageAdapter

        NG_pager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                val display = windowManager.defaultDisplay
                val outMetrics = DisplayMetrics()
                display.getMetrics(outMetrics)

                val dpWidth = outMetrics.widthPixels
                val ll: LinearLayout = findViewById(R.id.ll_help_button);
                val iHorzScroll = ((ll.width - dpWidth) / 2);
                val hsv: HorizontalScrollView? = findViewById(R.id.hsv_btn_help);


                try {
                    when (position) {
                        4 -> {
                            setButtonCheckStatus(false, false, false, false, true)
                            hsv?.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                        }
                        3 -> {
                            setButtonCheckStatus(false, false, false, true, false)
                            hsv?.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                        }
                        2 -> {
                            setButtonCheckStatus(false, false, true, false, false)
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
                } catch (e: Exception) {
                    //                   Toast.makeText(getApplicationContext(), "Cpc_Main btn not exists", Toast.LENGTH_SHORT).show();
                    Log.e("NG_help", "btn not exists")
                }

            }
        })



        btnGen = findViewById(R.id.btn_help_gen) as ToggleButton
        btnGen!!.setOnClickListener(
            View.OnClickListener
            {
                NG_pager!!.setCurrentItem(0)
                setButtonCheckStatus(true, false, false, false, false)
            })

        btnWidget = findViewById<ToggleButton>(R.id.btn_help_Widget)
        btnWidget!!.setOnClickListener(
            View.OnClickListener
            {
                NG_pager!!.setCurrentItem(1)
                setButtonCheckStatus(false, true, false, false, false)
            })

        btnCost = findViewById<ToggleButton>(R.id.btn_help_cost)
        btnCost!!.setOnClickListener(
            View.OnClickListener
            {
                NG_pager!!.setCurrentItem(2)
                setButtonCheckStatus(false, false, true, false, false)
            })

        btnWifi = findViewById<ToggleButton>(R.id.btn_help_Wifi)
        btnWifi!!.setOnClickListener(
            View.OnClickListener
            {
                NG_pager!!.setCurrentItem(3)
                setButtonCheckStatus(false, false, false, true, false)
            })
        btnApp = findViewById<ToggleButton>(R.id.btn_help_App)
        btnApp!!.setOnClickListener(
            View.OnClickListener
            {
                NG_pager!!.setCurrentItem(4)
                setButtonCheckStatus(false, false, false, false, true)
            })
        setButtonCheckStatus(true, false, false, false, false)
    }


    private
    val getFragments: MutableList<Fragment>
        get() {
            val fList = ArrayList<Fragment>()

            fList.add(NG_Frag_Help.newInstance("Fragment 1", "Gen"))
            fList.add(NG_Frag_Help.newInstance("Fragment 1", "Widget"))
            fList.add(NG_Frag_Help.newInstance("Fragment 1", "Cost"))
            fList.add(NG_Frag_Help.newInstance("Fragment 1", "WIFI"))
            fList.add(NG_Frag_Help.newInstance("Fragment 1", "App"))
            return fList
        }



    private fun setButtonCheckStatus(
        General: Boolean?,
        Widget: Boolean?,
        Cost: Boolean?,
        Wifi: Boolean?,
        App: Boolean?
    ) {
        btnGen?.setChecked(General!!)
        btnWidget?.setChecked(Widget!!)
        btnCost?.setChecked(Cost!!)
        btnWifi?.setChecked(Wifi!!)
        btnApp?.setChecked(App!!);
    };

    private inner class NG_PageAdapter(fm: FragmentManager, private val ng_fragments: List<Fragment>) :
        androidx.fragment.app.FragmentStatePagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return this.ng_fragments[position]
        }

        override fun getCount(): Int {
            return this.ng_fragments.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val tFr = super.instantiateItem(container, position) as Fragment
            NG_frag?.set(position, tFr)
            return tFr
        }
    }

}
