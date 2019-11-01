package de.franky.l.capricornng

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import androidx.preference.PreferenceDialogFragmentCompat
import android.os.Bundle



class NG_Dlg_NuPi_Dialog() : PreferenceDialogFragmentCompat() {

    private val iData: Int = 0
            var PickerData: NumberPicker? = null
    private val iDataDefVal: Int  = 0

            var PickerUnit: NumberPicker? = null
    lateinit var positiveResult: ()->Unit
    lateinit var CreateDialogView: (View   )->Unit


    override fun onDialogClosed(positiveResult: Boolean) {

        PickerData!!.clearFocus()

        if (positiveResult) {
            positiveResult();

        }
    }


    override fun onCreateDialogView(context: Context?): View {

        val inflater =
            NG_Application.applicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val myView = inflater.inflate(R.layout.ng_nupi_data_val, null)

        CreateDialogView(myView);


        return myView
    }



    companion object {
        fun newInstance(key: String): NG_Dlg_NuPi_Dialog {

        val fragment = NG_Dlg_NuPi_Dialog()
        val b = Bundle(1)
        b.putString(PreferenceDialogFragmentCompat.ARG_KEY, key)
        fragment.arguments =b

        return fragment
        }
    }
}
