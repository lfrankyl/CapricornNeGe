package de.franky.l.capricornng

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

import de.franky.l.capricornng.NG_Utils.NG_Pref
import java.util.ArrayList

class NG_Settings_Free_PhoneNumber : AppCompatActivity() {
    internal var sNewPhoneNumber: String =""
    internal lateinit var fab_Add: FloatingActionButton
    internal lateinit var fab_Delete: FloatingActionButton
    internal lateinit var fab_Info: FloatingActionButton
    internal lateinit var tv_Clone: TextView
    internal lateinit var FreePhoneNo: ArrayList<String>                                // Array mit den kostenlosen Telefonnummern


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ng_setting_free_phone_number)
        NG_Utils.changeTextSize(findViewById(R.id.ll_freePhoneNo) as ViewGroup)

        FreePhoneNo = ArrayList()


        tv_Clone = findViewById(R.id.tv_Clone) as TextView
        fab_Add = findViewById(R.id.fab_Add) as FloatingActionButton
        fab_Delete = findViewById(R.id.fab_Delete) as FloatingActionButton
        fab_Info = findViewById(R.id.fab_Info) as FloatingActionButton
        fab_Add.setOnClickListener(View.OnClickListener {
            showAddItemDialog(this@NG_Settings_Free_PhoneNumber,"",null )
        }
        )
        fab_Delete.setOnClickListener(View.OnClickListener {
            DeleteItemDialog(this@NG_Settings_Free_PhoneNumber,"",null )
        }
        )
        fab_Info.setOnClickListener(View.OnClickListener {
            InfoDialog(this@NG_Settings_Free_PhoneNumber,"",null )
        })
        getPhoneNumbersFromprefListArrayString()
        setPhoneNumbersToStringAndFillTextViews(findViewById(R.id.ll_PhoneNo) as ViewGroup)

    }

    override protected fun onPause() {
        // Log.d("Cpc_Settings_Free_PhoneNumber","onPause");
        super.onPause()
        putPhoneNumbersToArrayString(findViewById(R.id.ll_PhoneNo) as ViewGroup)
    }

    private fun getPhoneNumbersFromprefListArrayString() {
        FreePhoneNo = NG_Pref.getStringArray(getString(R.string.pref_Calls_FreeNumbArr_Key))
    }

    private fun setPhoneNumbersToStringAndFillTextViews(root: ViewGroup) {
        if ((root as LinearLayout).childCount > 0)
            root.removeAllViews()
        for (i in 0 until FreePhoneNo.size) {
            setPhoneNumberInTextView(FreePhoneNo.get(i), null, true)
        }
    }

    private fun putPhoneNumbersToArrayString(root: ViewGroup) {
        FreePhoneNo.clear()
        for (i in 0 until root.childCount) {
            val v = root.getChildAt(i)
            if (v is TextView) {
                FreePhoneNo.add(v.text.toString())
            }
        }
        NG_Pref.putStringArray(getString(R.string.pref_Calls_FreeNumbArr_Key), FreePhoneNo)
        NG_Pref.putInt(getString(R.string.pref_Calls_FreeNumb_Key), FreePhoneNo.size )
    }

    private fun DeleteItemDialog(c: Context, sText: String, vTextView: TextView?) {
        // setup the alert builder
        val builder = AlertDialog.Builder(c)
        builder.setTitle(R.string.pref_Calls_FreeNumb_Title_Delete)

        val strArray = arrayOfNulls<String>(FreePhoneNo.size)
        val checkedItems = BooleanArray(FreePhoneNo.size)
        for (i in strArray.indices) {
            strArray[i] = FreePhoneNo.get(i)
            checkedItems[i] = java.lang.Boolean.FALSE
        }
        // add a checkbox list
        builder.setMultiChoiceItems(strArray, checkedItems,
            DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                // user checked or unchecked a box
                checkedItems[which] = isChecked
            })

        // add OK and Cancel buttons
        builder.setPositiveButton( R.string.pref_Calls_FreeNumb_Apply,
            DialogInterface.OnClickListener { dialog, which ->
                // user clicked OK
                for (i in FreePhoneNo.size - 1 downTo 0) {
                    if (checkedItems[i]) {
                        FreePhoneNo.removeAt(i)
                    }
                }
                val llPhoNo = findViewById(R.id.ll_PhoneNo) as ViewGroup
                setPhoneNumbersToStringAndFillTextViews(llPhoNo)
            })
        builder.setNegativeButton(android.R.string.cancel, null)

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun showAddItemDialog(c: Context, sText: String, vTextView: TextView?) {
        val taskEditText = EditText(c)
        taskEditText.inputType = InputType.TYPE_CLASS_PHONE
        taskEditText.setText(sText)
        val dialog = AlertDialog.Builder(c)
            .setTitle(R.string.pref_Calls_FreeNumb_Title)
            .setMessage(R.string.pref_Calls_FreeNumb_Task)
            .setView(taskEditText)
            .setPositiveButton(R.string.pref_Calls_FreeNumb_Apply, DialogInterface.OnClickListener { dialog, which ->
                    sNewPhoneNumber = taskEditText.text.toString()
                    setPhoneNumberInTextView(sNewPhoneNumber, vTextView, false)
                })
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    private fun InfoDialog(c: Context, sText: String, vTextView: TextView?) {
        val dialog = AlertDialog.Builder(c)
            .setTitle(R.string.pref_Calls_FreeNumb_Title)
            .setMessage( getString(R.string.pref_Calls_FreeNumb_Info1) + "\n\n" +
                        getString(R.string.pref_Calls_FreeNumb_Info2) + "\n\n" +
                        getString(R.string.pref_Calls_FreeNumb_Info3))
            .setPositiveButton(android.R.string.ok, null)
            .create()
        dialog.show()
    }

    private fun setPhoneNumberInTextView( sNewPhoneNo: String, tv_PhoneNo: TextView?, Init: Boolean ) {
        var tv_PhoneNo = tv_PhoneNo
        val ll_PhNo = findViewById(R.id.ll_PhoneNo) as LinearLayout
        if (tv_PhoneNo == null) {
            tv_PhoneNo = TextView(this@NG_Settings_Free_PhoneNumber)
            tv_PhoneNo.layoutParams = tv_Clone.layoutParams
            tv_PhoneNo.setOnClickListener { v ->
                showAddItemDialog(this@NG_Settings_Free_PhoneNumber,  sNewPhoneNo,  v as TextView )
            }
            ll_PhNo.addView(tv_PhoneNo)
            NG_Utils.changeTextSize(ll_PhNo)
            if (!Init) {
                FreePhoneNo.add(sNewPhoneNo)
            }
        }
        tv_PhoneNo!!.text = sNewPhoneNo
    }
}