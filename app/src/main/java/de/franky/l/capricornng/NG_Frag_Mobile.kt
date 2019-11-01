package de.franky.l.capricornng

import android.content.Context
import android.net.TrafficStats
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.franky.l.capricornng.NG_Utils.NG_Val_Time_Cots
import de.franky.l.capricornng.NG_Utils.NG_Val_Mob_Data


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NG_Frag_Mobile.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NG_Frag_Mobile.newInstance] factory method to
 * create an instance of this fragment.
 */
class NG_Frag_Mobile : androidx.fragment.app.Fragment() {

    private var mParam1: String? = null
    private var mParam2: String? = null

    private var Mobile_View: View? = null
    private var Lout_Container: ViewGroup? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        Log.d("Cpc_frag_mobile","onCreate");
        retainInstance = true
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onStart() {
        super.onStart()
        //        Log.d("Cpc_frag_mobile","onStart");
        FillMobilePage()
        NG_Utils.changeTextSize(Lout_Container)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Mobile_View = inflater.inflate(R.layout.ng_frag_mobile, container, false)
        Lout_Container = Mobile_View!!.findViewById(R.id.cpc_main)
        return Mobile_View
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    private fun FillMobilePage() {
        val lMobileReceivedBytes = TrafficStats.getMobileRxBytes()
        val lMobileSendBytes = TrafficStats.getMobileTxBytes()
        val AppContext = NG_Application.applicationContext()
        val sMobileCycleOptions = AppContext.getResources().getStringArray(R.array.MobileCycleOptionArray)

        NG_Utils_Data_Constraints.CalcMonitoringConstraints(AppContext)
        NG_Utils_Data_Val.Get_Mob_Data_Values(AppContext)

        SetTextViewText(R.id.tV_C_V1, R.string.ng_nd)

        if (NG_Utils.NG_Val_Time_Cots.iDataCalulation()==2)
        // wenn Abrechnungszeitraum eingestellt ist
        {
            SetTextViewText(R.id.tV_C_V1, sMobileCycleOptions[NG_Val_Time_Cots.iMobileCycleOption()])

            SetTextViewText(
                R.id.tV_C_V2,
                "(" + AppContext.getString(R.string.pref_Cur_Bill_Period_Title2) + " " + Integer.toString(NG_Val_Time_Cots.iCycleLength() - NG_Val_Time_Cots.iCycleDone()) + " " + AppContext.getString(
                    R.string.pref_Cur_Bill_Period_Title3
                ) + ")"
            )
            SetTextViewText(
                R.id.tV_C_V3,
                android.text.format.DateFormat.format("dd.MM.yyyy", NG_Val_Time_Cots.DateOfChange()).toString()
            )
            val lMobMax = NG_Val_Mob_Data.lNuPiMobMaxVal()// Maxwert in Byte
            SetTextViewText(
                R.id.tV_M_H2,
                AppContext.getString(R.string.pref_MobileMaxVal) + ": " + NG_Utils.MakeOutString(lMobMax.toDouble()) + NG_Utils.CalcUnit(
                    lMobMax.toDouble())
            )
        }

        SetTextViewText(
            R.id.tV_M_V1,
            NG_Utils.MakeOutString(NG_Val_Mob_Data.dMobileTodayMax()) + NG_Utils.CalcUnit(NG_Val_Mob_Data.dMobileTodayMax())
        )
        SetTextViewText(R.id.tV_M_V2, NG_Utils.MakeOutString(NG_Val_Mob_Data.dMobileTodayCur()) + NG_Utils.CalcUnit(NG_Val_Mob_Data.dMobileTodayCur()))

        SetTextViewText(
            R.id.tV_M_V3,
            NG_Utils.MakeOutString(NG_Val_Mob_Data.dMobileDayMax()) + NG_Utils.CalcUnit(NG_Val_Mob_Data.dMobileDayMax())
        )
        SetTextViewText(
            R.id.tV_M_V4,
            NG_Utils.MakeOutString(NG_Val_Mob_Data.dMobileDayCur()) + NG_Utils.CalcUnit(NG_Val_Mob_Data.dMobileDayCur())
        )
        SetTextViewText(
            R.id.tV_M_V5,
            NG_Utils.MakeOutString(lMobileReceivedBytes.toDouble() + lMobileSendBytes) + NG_Utils.CalcUnit(
                lMobileReceivedBytes.toDouble() + lMobileSendBytes
            )
        )

        SetTextViewText(
            R.id.tV_M_V6,
            NG_Utils.MakeOutString(lMobileReceivedBytes.toDouble()) + NG_Utils.CalcUnit(lMobileReceivedBytes.toDouble())
        )
        SetTextViewText(
            R.id.tV_M_V7,
            NG_Utils.MakeOutString(lMobileSendBytes.toDouble()) + NG_Utils.CalcUnit(lMobileSendBytes.toDouble())
        )

        SetTextViewText(R.id.tV_M_T8, R.string.ng_blankChr)
        SetTextViewText(R.id.tV_M_V8, R.string.ng_blankChr)
        SetTextViewText(R.id.tV_M_T9, R.string.ng_blankChr)
        SetTextViewText(R.id.tV_M_V9, R.string.ng_blankChr)
    }

    internal fun SetTextViewText(iTV: Int, iText: Int) {
        val TV = Mobile_View!!.findViewById<TextView>(iTV)
        TV.setText(iText)
    }

    internal fun SetTextViewText(iTV: Int, sText: String) {
        val TV = Mobile_View!!.findViewById<TextView>(iTV)
        TV.text = sText
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Cpc_frag_mobile.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): NG_Frag_Mobile {
            val fragment = NG_Frag_Mobile()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.setArguments(args)
            return fragment
        }
    }

}// Required empty public constructor