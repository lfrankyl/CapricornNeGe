package de.franky.l.capricornng

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import java.util.ArrayList




class NG_Activity_InfoHeader : AppCompatActivity() {

    private var btnImpr: ToggleButton? = null
    private var btnOSS: ToggleButton? = null

    private var NG_frag: MutableList<Fragment>? = null
    private var NG_pager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.ng_impr_screen)
        NG_frag = getFragments

        val pageAdapter = NG_PageAdapter(supportFragmentManager, NG_frag!!)
        NG_pager = findViewById<ViewPager>(R.id.ng_viewpager_impr)
        NG_pager!!.adapter =pageAdapter

        NG_pager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {


                try {
                    when (position) {
                        1 -> {
                            setButtonCheckStatus(false, true)
                        }
                        else                            // includes 0 (Standard)
                        -> {
                            setButtonCheckStatus(true, false)
                        }
                    }
                } catch (e: Exception) {
                    //                   Toast.makeText(getApplicationContext(), "Cpc_Main btn not exists", Toast.LENGTH_SHORT).show();
                    Log.e("NG_impr", "btn not exists")
                }

            }
        })



        btnImpr = findViewById(R.id.btn_impr_gen) as ToggleButton
        btnImpr!!.setOnClickListener(
            View.OnClickListener
            {
                NG_pager!!.setCurrentItem(0)
                setButtonCheckStatus(true, false)
            })

        btnOSS = findViewById<ToggleButton>(R.id.btn_impr_OSS)
        btnOSS!!.setOnClickListener(
            View.OnClickListener
            {
                NG_pager!!.setCurrentItem(1)
                setButtonCheckStatus(false, true)
            })

        setButtonCheckStatus(true, false)
    }


    private
    val getFragments: MutableList<Fragment>
        get() {
            val fList = ArrayList<Fragment>()

            fList.add(NG_Frag_InfoImpr.newInstance("Fragment 1", "Impr"))
            fList.add(NG_Frag_InfoImpr.newInstance("Fragment 1", "OSS"))
            return fList
        }



    private fun setButtonCheckStatus(
        Impr: Boolean?,
        OSS: Boolean?
    ) {
        btnImpr?.setChecked(Impr!!)
        btnOSS?.setChecked(OSS!!)
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
