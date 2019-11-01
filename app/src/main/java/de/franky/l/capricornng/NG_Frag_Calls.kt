package de.franky.l.capricornng

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.franky.l.capricornng.NG_Utils.NG_Pref
import de.franky.l.capricornng.NG_Utils.NG_Val_Time_Cots


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Cpc_frag_calls.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Cpc_frag_calls.newInstance] factory method to
 * create an instance of this fragment.
 */
class NG_Frag_Calls : androidx.fragment.app.Fragment()  {

    private var mParam1: String? = null
    private var mParam2: String? = null

    private var Calls_View: View? = null
    private var Layout_Container: ViewGroup? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        Log.d("Cpc_frag_calls","onCreate");
        setRetainInstance(true)
        if (getArguments() != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onStart() {
        super.onStart()
        //        Log.d("Cpc_frag_calls","onStart");
        FillCallsPage()
        NG_Utils.changeTextSize(Layout_Container)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        Calls_View = inflater.inflate(R.layout.ng_frag_mobile, container, false)
        Layout_Container = Calls_View!!.findViewById(R.id.cpc_main)
        return Calls_View
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

    private fun FillCallsPage() {
        val AppContext = NG_Application.applicationContext()
        val sMobileCycleOptions = AppContext.getResources().getStringArray(R.array.MobileCycleOptionArray)

        NG_Utils_Data_Constraints.CalcMonitoringConstraints(AppContext)
        NG_Utils_Data_Val.Get_Mob_Data_Values(AppContext)

        SetTextViewText(R.id.tV_C_V1, R.string.ng_nd)

        if (NG_Val_Time_Cots.iDataCalulation()==2)
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
            val lCallsMax = NG_Pref.getLong(R.string.pref_NuPiMax_Calls_Key, R.string.pref_Calls_MaxVal_Default) // Maxwert in Minuten
            SetTextViewText(R.id.tV_M_H2,AppContext.getString(R.string.pref_Calls_MaxVal) + ": " + lCallsMax.toString() + " " + AppContext.getString(R.string.pref_Calls_RefVal_Unit))
        }
/*            SetTextViewText(
                R.id.tV_M_H2,
                AppContext.getString(R.string.pref_Calls_MaxVal) + ": " + lCallsMax.toString() + " " + AppContext.getString(
                    R.string.pref_Calls_RefVal_Unit
                )
            )
        }

        SetTextViewText(R.id.tV_M_H3, R.string.pref_CurVal_CallDataInfo2)

        val mIV = Mobile_View!!.findViewById(R.id.iV_M2)
        mIV.setImageResource(R.drawable.cpc_phone_gen_frag)

        SetTextViewText(R.id.tV_M_T1, R.string.pref_CurVal_MobileMax)
        SetTextViewText(
            R.id.tV_M_V1,
            NG_Utils.MakeOutString(Cpc_Utils.CurVal.dCallsMinOutgoingTodayMax) + " " + AppContext.getResources().getString(
                R.string.pref_CurVal_Call_Unit
            )
        )

        SetTextViewText(R.id.tV_M_T2, R.string.pref_CurVal_MobileCur)
        SetTextViewText(
            R.id.tV_M_V2,
            Cpc_Utils.CurVal.DispData[7].getValue() + " " + AppContext.getResources().getString(R.string.pref_CurVal_Call_Unit) + " / " + String.valueOf(
                Cpc_Utils.CurVal.iCallsNumOutgoing
            ) + " " + AppContext.getResources().getString(R.string.pref_CurVal_CallDataInfo1)
        )

        SetTextViewText(R.id.tV_M_T3, R.string.pref_CurVal_MobileMeanMax)
        SetTextViewText(
            R.id.tV_M_V3,
            Cpc_Utils.MakeOutString(Cpc_Utils.CurVal.dCallsMinOutgoingDayMax) + " " + AppContext.getResources().getString(
                R.string.pref_CurVal_Call_Unit
            )
        )

        SetTextViewText(R.id.tV_M_T4, R.string.pref_CurVal_MobileMeanCur)
        SetTextViewText(
            R.id.tV_M_V4,
            Cpc_Utils.MakeOutString(Cpc_Utils.CurVal.dCallsMinOutgoingDayCur) + " " + AppContext.getResources().getString(
                R.string.pref_CurVal_Call_Unit
            )
        )

        SetTextViewText(R.id.tV_M_H4, R.string.blankChr)

        SetTextViewText(R.id.tV_M_T5, R.string.pref_CurVal_CallDataInfo3)
        if (Cpc_Utils.CurVal.dCallsMinIncoming < 10) {
            SetTextViewText(
                R.id.tV_M_V5,
                "0" + Cpc_Utils.MakeOutString(Cpc_Utils.CurVal.dCallsMinIncoming) + " " + AppContext.getResources().getString(
                    R.string.pref_CurVal_Call_Unit
                ) + " / " + String.valueOf(Cpc_Utils.CurVal.iCallsNumIncoming) + " " + AppContext.getResources().getString(
                    R.string.pref_CurVal_CallDataInfo1
                )
            )
        } else {
            SetTextViewText(
                R.id.tV_M_V5,
                Cpc_Utils.MakeOutString(Cpc_Utils.CurVal.dCallsMinIncoming) + " " + AppContext.getResources().getString(
                    R.string.pref_CurVal_Call_Unit
                ) + " / " + String.valueOf(Cpc_Utils.CurVal.iCallsNumIncoming) + " " + AppContext.getResources().getString(
                    R.string.pref_CurVal_CallDataInfo1
                )
            )
        }
        SetTextViewText(R.id.tV_M_T6, R.string.pref_CurVal_CallDataInfo4)
        SetTextViewText(
            R.id.tV_M_V6,
            String.valueOf(Cpc_Utils.CurVal.iCallsNumMissed) + " " + AppContext.getResources().getString(
                R.string.pref_CurVal_CallDataInfo1
            )
        )

        SetTextViewText(R.id.tV_M_H5, R.string.blankChr)

        SetTextViewText(R.id.tV_M_T7, R.string.blankChr)

        SetTextViewText(R.id.tV_M_V7, R.string.blankChr)

        if (Cpc_Utils.CurVal.bCallsIntegrateSMS) {
            SetTextViewText(R.id.tV_M_T8, R.string.pref_CurVal_CallDataInfo5)
            SetTextViewText(R.id.tV_M_V8, String.valueOf(Cpc_Utils.CurVal.iSMSNumSent))
        } else {
            SetTextViewText(R.id.tV_M_T8, R.string.blankChr)
            SetTextViewText(R.id.tV_M_V8, R.string.blankChr)
        }
        SetTextViewText(R.id.tV_M_T9, R.string.blankChr)
        SetTextViewText(R.id.tV_M_V9, R.string.blankChr)
 */
    }

        internal fun SetTextViewText(iTV: Int, iText: Int) {
            val TV = Calls_View!!.findViewById<TextView>(iTV)
            TV.setText(iText)
        }

        internal fun SetTextViewText(iTV: Int, sText: String) {
            val TV = Calls_View!!.findViewById<TextView>(iTV)
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
        fun newInstance(param1: String, param2: String): NG_Frag_Calls {
            val fragment = NG_Frag_Calls()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.setArguments(args)
            return fragment
        }
    }

}// Required empty public constructor