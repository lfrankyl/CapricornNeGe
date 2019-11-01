package de.franky.l.capricornng

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ng_frag_help.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ng_frag_help.newInstance] factory method to
 * create an instance of this fragment.
 */
class NG_Frag_InfoImpr : androidx.fragment.app.Fragment() {

    private var mParam1: String? = null
    private var mParam2: String? = null

    private var Impr_View: View? = null
    private var Lout_Container: ViewGroup? = null
    private var wv_impr: WebView? = null

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
        FillImprPage()
        NG_Utils.changeTextSize(Lout_Container)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Impr_View = inflater.inflate(R.layout.ng_frag_impr, container, false)
        Lout_Container = Impr_View!!.findViewById(R.id.ng_impr)
        wv_impr = Impr_View!!.findViewById<WebView>(R.id.wv_impr)
        return Impr_View
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
    private fun FillImprPage() {

        val myContext = NG_Application.applicationContext()

        when (mParam2){
          "Impr" ->  {
              val sOsInfo: String
              var sLastUpdate = "11.11.11 ; 12:12"
              var sInfo = "x.y"
              var sAddInfo = "; VwC = "
              try {
                  sInfo = myContext.packageManager.getPackageInfo(myContext.packageName, 0).versionName
                  sLastUpdate =
                      SimpleDateFormat("dd.MM.yyyy' ;   'HH:mm", Locale.getDefault()).format(BuildConfig.TIMESTAMP)
              } catch (e: Exception) {
                  Toast.makeText(myContext, "Exception @ Capricorn_SettingsFagment", Toast.LENGTH_SHORT).show()
              }
              sAddInfo = sAddInfo + BuildConfig.WithCalls.toString()    // Shows if Calls are supported or not

              sOsInfo = "  $sInfo ;   $sLastUpdate $sAddInfo"
              val instream = myContext.getAssets().open(myContext.getString(R.string.file_impr_Gen));
              val r = InputStreamReader(instream);
              var swebpage = ConvertToString(r);
              swebpage = swebpage.replace(myContext.getString(R.string.VersionReplaceString),sOsInfo,false);
              wv_impr?.loadData(swebpage, "text/html; charset=utf-8", "utf-8")
          };
          "OSS" ->  {
              wv_impr?.loadUrl(myContext.getString(R.string.file_impr_OSS))
          };

        }
    }
    private fun ConvertToString(IstreamR: InputStreamReader) :String{
        val sb = StringBuilder()
        var line: String?
        val br = BufferedReader(IstreamR)
        line = br.readLine()
        while (line != null) {
            sb.append(line)
            line = br.readLine()
        }
        br.close()
        return(sb.toString())
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
        fun newInstance(param1: String, param2: String): NG_Frag_InfoImpr {
            val fragment = NG_Frag_InfoImpr()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.setArguments(args)
            return fragment
        }
    }

}// Required empty public constructor
