package de.franky.l.capricornng

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ng_frag_help.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ng_frag_help.newInstance] factory method to
 * create an instance of this fragment.
 */
class NG_Frag_Help : androidx.fragment.app.Fragment() {

    private var mParam1: String? = null
    private var mParam2: String? = null

    private var Help_View: View? = null
    private var Lout_Container: ViewGroup? = null
    private var wv_help: WebView? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        Log.d("Cpc_frag_wlan","onCreate");
        retainInstance = true
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onStart() {
        super.onStart()
        //        Log.d("Cpc_frag_wlan","onStart");
        FillHelpPage()
        NG_Utils.changeTextSize(Lout_Container)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Help_View = inflater.inflate(R.layout.ng_frag_help, container, false)
        Lout_Container = Help_View!!.findViewById(R.id.ng_help)
        wv_help = Help_View!!.findViewById<WebView>(R.id.wv_help)
        return Help_View
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
    private fun FillHelpPage() {

        val myContext = NG_Application.applicationContext()

        when (mParam2){
          "Gen" ->  wv_help?.loadUrl(myContext.getString(R.string.file_help_Gen));
          "Widget" ->  wv_help?.loadUrl(myContext.getString(R.string.file_help_Widget));
          "Cost" ->  wv_help?.loadUrl(myContext.getString(R.string.file_help_Cost));
          "WIFI" ->  wv_help?.loadUrl(myContext.getString(R.string.file_help_Wifi));
          "App" ->  wv_help?.loadUrl(myContext.getString(R.string.file_help_App));

        }
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
         * @return A new instance of fragment Cpc_frag_wlan.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): NG_Frag_Help {
            val fragment = NG_Frag_Help()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.setArguments(args)
            return fragment
        }
    }

}// Required empty public constructor
