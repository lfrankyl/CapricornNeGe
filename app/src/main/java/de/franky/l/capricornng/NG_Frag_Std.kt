package de.franky.l.capricornng

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.Calendar
import java.util.GregorianCalendar


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Cpc_frag_std.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Cpc_frag_std.newInstance] factory method to
 * create an instance of this fragment.
 */
class NG_Frag_Std : androidx.fragment.app.Fragment() {

    private var mParam1: String? = null
    private var mParam2: String? = null

    private var Std_View: View? = null
    private var Lout_Container: ViewGroup? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.d("Cpc_frag_std","onCreate");
        retainInstance = true
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onStart() {
        super.onStart()
        //  Log.d("Cpc_frag_std","onStart");
        FillStdPage()
        NG_Utils.changeTextSize(Lout_Container)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Std_View = inflater.inflate(R.layout.ng_frag_std, container, false)
        Lout_Container = Std_View!!.findViewById(R.id.cpc_main)
        //    Log.d("Cpc_frag_std","onCreateView");
        return Std_View
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //      Log.d("Cpc_frag_std","onAttach");
        if (context is OnFragmentInteractionListener) {
            mListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        //        Log.d("Cpc_frag_std","onDetach");
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

    private fun FillStdPage() {
        val myContext = NG_Application.applicationContext()
        val sViewOptions = myContext.getResources().getStringArray(R.array.viewoptions)

        SetTextViewText(R.id.tV_S_T1, sViewOptions[0] + ": ")
        SetTextViewText(R.id.tV_S_V1, NG_Utils_Std_Values.igetCPUload(myContext).toString() + "%")

        SetTextViewText(R.id.tV_S_T2, sViewOptions[4] + ": ")
        SetTextViewText(R.id.tV_S_V2, NG_Utils_Std_Values.igetBatLevel(myContext).toString() + "%")

        SetTextViewText(R.id.tV_S_T3, sViewOptions[7] + ": ")
        SetTextViewText(R.id.tV_S_V3, NG_Utils_Std_Values.igetBrightness(myContext).toString() + "%")

        SetTextViewText(R.id.tV_S_T4, sViewOptions[1] + ": ")
        SetTextViewText(R.id.tV_S_V4, NG_Utils.MakeOutString(NG_Utils_Std_Values.dGetAvaiableRAM(myContext)) + NG_Utils.CalcUnit(NG_Utils_Std_Values.dGetAvaiableRAM(myContext)))
        SetTextViewText(R.id.tV_S_V4_2,NG_Utils.MakeOutString(NG_Utils_Std_Values.dGetTotalRAM(myContext)) + NG_Utils.CalcUnit(NG_Utils_Std_Values.dGetTotalRAM(myContext)))

        SetTextViewText(R.id.tV_S_T5, sViewOptions[2] + ": ")
        SetTextViewText(R.id.tV_S_V5, NG_Utils.MakeOutString(NG_Utils_Std_Values.dGetAvaiableInternalSDCArd(myContext)) + NG_Utils.CalcUnit(NG_Utils_Std_Values.dGetAvaiableInternalSDCArd(myContext)))
        SetTextViewText(R.id.tV_S_V5_2,NG_Utils.MakeOutString(NG_Utils_Std_Values.dGetTotalInternalSDCArd(myContext)) + NG_Utils.CalcUnit(NG_Utils_Std_Values.dGetTotalInternalSDCArd(myContext)))

        SetTextViewText(R.id.tV_S_T6, sViewOptions[3] + ": ")
        SetTextViewText(R.id.tV_S_V6, NG_Utils.MakeOutString(NG_Utils_Std_Values.dGetAvaiableExternalSDCArd(myContext)) + NG_Utils.CalcUnit(NG_Utils_Std_Values.dGetAvaiableExternalSDCArd(myContext)))
        SetTextViewText(R.id.tV_S_V6_2,NG_Utils.MakeOutString(NG_Utils_Std_Values.dGetTotalExternalSDCArd(myContext)) + NG_Utils.CalcUnit(NG_Utils_Std_Values.dGetTotalExternalSDCArd(myContext)))

        SetTextViewText(R.id.tV_S_V7, myContext.getString(R.string.pref_CurVal_LastBoot) + " " + GetLastBootDate(myContext))

        SetTextViewText(R.id.tV_S_SDK_H, myContext.getString(R.string.pref_CurVal_Title_SDK) + ":")

        SetTextViewText(
            R.id.tV_S_SDK_V, Build.VERSION.RELEASE.toString() + "  " +
                    Build.VERSION.SDK_INT.toString() + "  " +
                    Build.VERSION.CODENAME.toString()
        )
        SetTextViewText(R.id.textView8, Build.VERSION.INCREMENTAL.toString())
        SetTextViewText(R.id.textView10, Build.BRAND)
        SetTextViewText(R.id.textView12, Build.MANUFACTURER)
        SetTextViewText(R.id.textView14, Build.MODEL)
        SetTextViewText(R.id.textView16, Build.PRODUCT)
        SetTextViewText(R.id.textView18, Build.BOARD)
        SetTextViewText(R.id.textView20, Build.DISPLAY)
        @SuppressLint("Deprecation")

        if(Build.VERSION.SDK_INT <= (Build.VERSION_CODES.O)) {
            @Suppress("DEPRECATION")
            SetTextViewText(R.id.textView22, Build.SERIAL)
        }
        else
        {
            SetTextViewText(R.id.textView22, getString(R.string.ng_NotKnown))
        }
    }

    private fun GetLastBootDate(myContext: Context): String {
        var lLastBoot = SystemClock.elapsedRealtime() / 1000
        val myCal = GregorianCalendar()
        val sDate: String;
        if (lLastBoot <= Integer.MAX_VALUE) {
            lLastBoot = lLastBoot * -1
            myCal.add(Calendar.SECOND, lLastBoot.toInt())
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                sDate = android.text.format.DateFormat.format("dd.MM.yyyy ; kk:mm", myCal.time).toString()
            } else {
                sDate = android.text.format.DateFormat.format("dd.MM.yyyy ; HH:mm", myCal.time).toString()

            }
        } else {
            sDate = myContext.getResources().getString(R.string.ng_nd)
        }
        return sDate

    }

    internal fun SetTextViewText(iTV: Int, sText: String) {
        val TV = Std_View!!.findViewById<TextView>(iTV)
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
         * @return A new instance of fragment Cpc_frag_std.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): NG_Frag_Std {
            val fragment = NG_Frag_Std()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.setArguments(args)
            //Log.d("Cpc_frag_std","newInstance");
            return fragment
        }
    }


}// Required empty public constructor
