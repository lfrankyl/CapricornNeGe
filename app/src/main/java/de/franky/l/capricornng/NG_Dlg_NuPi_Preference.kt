package de.franky.l.capricornng

import android.content.Context
import android.content.res.TypedArray
import android.os.Parcel
import android.os.Parcelable
import androidx.preference.DialogPreference
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import androidx.preference.Preference

class NG_Dlg_NuPi_Preference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {

    init {
        dialogLayoutResource = R.layout.ng_nupi_data_val
    }


}
