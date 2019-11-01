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

class NG_Dlg_NuPi__________Obsolete(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {

    private val iwlanData: Int = 0
    private var PickwlanData: NumberPicker? = null
    private val iwlanDataDefVal: Int

    private var PickwlanUnit: NumberPicker? = null

    init {

        // Log.d("Cpc_Dlg_NuPi_WLAN_RefVal","Cpc_Dlg_NuPi_WLAN_RefVal");
        val sDef_Val = context.getString(R.string.pref_wlanRefVal_Default)
        iwlanDataDefVal = sDef_Val.toInt();

        dialogLayoutResource = R.layout.ng_nupi_data_val

    }


//    override fun onDialogClosed(positiveResult: Boolean) {
//
//        // Log.d("onDialogClosed",String.valueOf(PickerData.getValue()));
//        PickerData!!.clearFocus()
//        if (positiveResult) {
//            //val lwlanValueByte = Cpc_Utils.lCalcDataValToByte(PickerData!!.value, PickerUnit!!.value)
//            val lwlanValueByte = 25253644343454;
//            persistLong(lwlanValueByte)
//            this.callChangeListener(lwlanValueByte)
//
//        }
//    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any) {
        if (!restorePersistedValue) {
            persistLong(iwlanDataDefVal.toLong())
        }
    }


    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        // Log.d("Cpc_Dlg_NuPi_MobData_RevVal","onGetDefaultValue");
        return a.getInteger(index, iwlanDataDefVal)
    }


//    override fun onCreateDialogView(): View {
//        val sUnitValues: Array<String>
//        sUnitValues = NG_Application.applicationContext().getResources().getStringArray(R.array.pref_DataUnits)
//
//        val iWlanDataMax = 9999
//        val iWlanDataMin = 0
//
//        val iWlanUnitMax = sUnitValues.size - 1
//        val iWlanUnitMin = 0
//        val lPersistedLong: Long
//        val iWlanPickerValueNeu: Int
//        val iwlanPickerUnitNeu: Int
//
//        // Log.d("Cpc_Dlg_NuPi_MobData_RevVal","onCreateDialogView");
//
//        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val view = inflater.inflate(R.layout.ng_nupi_data_val, null)
//
//        val mTV = view.findViewById(R.id.tv_nupi_refval_explanation) as TextView;
//        mTV.setText(NG_Application.applicationContext().getResources().getString(R.string.pref_wlanRefVal_Explanation))
//
//        PickerData = view.findViewById(R.id.nupi_refval_value)
//
//        // Initialize state
//        PickerData!!.maxValue = iWlanDataMax
//        PickerData!!.minValue = iWlanDataMin
//
//        // Log.d("onCreateDialogView",String.valueOf(iwlanDataDefVal));
//        lPersistedLong = this.getPersistedLong(iwlanDataDefVal.toLong())
//        iWlanPickerValueNeu = NG_Utils.iCalcDataVal(lPersistedLong)
//        iwlanPickerUnitNeu = NG_Utils.iCalcDataUnitIndex(lPersistedLong)
//
//        //Log.d("onCreateDialogView",String.valueOf(iWlanPickerValueNeu));
//        PickerData!!.value = iWlanPickerValueNeu
//        PickerData!!.wrapSelectorWheel = true
//
//        PickerUnit = view.findViewById(R.id.nupi_refval_unit)
//
//        // Initialize state
//        PickerUnit!!.maxValue = iWlanUnitMax
//        PickerUnit!!.minValue = iWlanUnitMin
//        PickerUnit!!.value = iwlanPickerUnitNeu
//        PickerUnit!!.wrapSelectorWheel = true  // Works only on SKD >=16,
//        PickerUnit!!.displayedValues = sUnitValues
//
//        return view
//    }


    //  This code copied from android's settings guide.
    private class SavedState : Preference.BaseSavedState {
        // Member that holds the setting's value
        // Change this data type to match the type saved by your Preference
        internal var value: Int = 0

        constructor(superState: Parcelable) : super(superState) {}

        constructor(source: Parcel) : super(source) {
            // Get the current preference's value
            value = source.readInt()  // Change this to read the appropriate data type
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            // Write the preference's value
            dest.writeInt(value)  // Change this to write the appropriate data type
        }

        // Standard creator object using an instance of this class
        //        public static final Parcelable.Creator<SavedState> CREATOR =
        //                new Parcelable.Creator<SavedState>() {
        //
        //            public SavedState createFromParcel(Parcel in) {
        //                return new SavedState(in);
        //            }
        //
        //            public SavedState[] newArray(int size) {
        //                return new SavedState[size];
        //            }
        //        };
    }


    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent) {
            // No need to save instance state since it's persistent, use superclass state
            return superState
        }

        // Create instance of custom BaseSavedState
        val myState = SavedState(superState)
        // Set the state's value with the class member that holds current setting value
        myState.value = iwlanData
        return myState
    }


    override fun onRestoreInstanceState(state: Parcelable?) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || state.javaClass != SavedState::class.java) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state)
            return
        }

        // Cast state to custom BaseSavedState and pass to superclass
        val myState = state as SavedState?
        super.onRestoreInstanceState(myState!!.getSuperState())

        // Set this Preference's widget to reflect the restored state
        PickwlanData!!.value = myState.value
    }
}
