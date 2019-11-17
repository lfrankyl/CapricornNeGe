package de.franky.l.capricornng

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.franky.l.capricornng.NG_Utils.NG_Val_Wifi


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Cpc_frag_wlan.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Cpc_frag_wlan.newInstance] factory method to
 * create an instance of this fragment.
 */
class NG_Frag_Wlan : androidx.fragment.app.Fragment() {

    private var mParam3: String? = null
    private var mParam4: String? = null

    private var Wlan_View: View? = null
    private var Lout_Container: ViewGroup? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        Log.d("Cpc_frag_wlan","onCreate");
        retainInstance = true
        if (arguments != null) {
            mParam3 = arguments!!.getString(ARG_PARAM3)
            mParam4 = arguments!!.getString(ARG_PARAM4)
        }
    }

    override fun onStart() {
        super.onStart()
        //        Log.d("Cpc_frag_wlan","onStart");
        FillWlanPage()
        NG_Utils.changeTextSize(Lout_Container)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Wlan_View = inflater.inflate(R.layout.ng_frag_wlan, container, false)
        Lout_Container = Wlan_View!!.findViewById(R.id.cpc_main)
        return Wlan_View
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

    private fun FillWlanPage() {

        SetTextViewText(R.id.tV_W_V1,NG_Utils.MakeOutString(NG_Val_Wifi.Wifi_Total().toDouble() ) + NG_Utils.CalcUnit(NG_Val_Wifi.Wifi_Total().toDouble()))
        SetTextViewText(R.id.tV_W_V2,NG_Utils.MakeOutString(NG_Val_Wifi.Wifi_Send().toDouble()) + NG_Utils.CalcUnit(NG_Val_Wifi.Wifi_Send().toDouble()))
        SetTextViewText(R.id.tV_W_V3,NG_Utils.MakeOutString(NG_Val_Wifi.Wifi_Received().toDouble()) + NG_Utils.CalcUnit(NG_Val_Wifi.Wifi_Received().toDouble()))
        SetTextViewText(R.id.tV_W_V5,NG_Utils.MakeOutString(NG_Val_Wifi.Wifi_MeasuredValue().toDouble()) + NG_Utils.CalcUnit(NG_Val_Wifi.Wifi_MeasuredValue().toDouble()) )
        SetTextViewText(R.id.tV_W_V6, NG_Val_Wifi.sWifiStartDate())     // WLAN-Datum  aus sharedpreference
    }

    internal fun SetTextViewText(iTV: Int, sText: String) {
        val TV = Wlan_View!!.findViewById<TextView>(iTV)
        TV.text = sText
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM3 = "param3"
        private val ARG_PARAM4 = "param4"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Cpc_frag_wlan.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): NG_Frag_Wlan {
            val fragment = NG_Frag_Wlan()
            val args = Bundle()
            args.putString(ARG_PARAM3, param1)
            args.putString(ARG_PARAM4, param2)
            fragment.setArguments(args)
            return fragment
        }
    }

}// Required empty public constructor
