package de.franky.l.capricornng

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat

import java.util.ArrayList

/**
 * Created by franky on 12.02.2017.
 */

object NG_Action_Permission_Helper {

    var iPermissionTelStatus = -1
    var iPermissionSMSStatus = -1


    fun RequestPermission(myAct: Activity) {
        val CrLf = System.getProperty("line.separator")
        val permissionsNeeded = ArrayList<String>()
        val myActivity: Activity
        myActivity = myAct

        val permissionsList = ArrayList<String>()
        if (addPermission(myAct, permissionsList, Manifest.permission.READ_CALL_LOG))
            permissionsNeeded.add(NG_Application.applicationContext().getResources().getString(R.string.Perm_Calls_Rationale))
        if (addPermission(myAct, permissionsList, Manifest.permission.READ_SMS))
            permissionsNeeded.add(NG_Application.applicationContext().getResources().getString(R.string.Perm_SMS_Rationale))

        if (permissionsList.size > 0) {
            if (permissionsNeeded.size > 0) {
                // Need Rationale
                var message = permissionsNeeded[0]
                for (i in 1 until permissionsNeeded.size)
                    message = message + CrLf + CrLf + permissionsNeeded[i]

                val builder1 = AlertDialog.Builder(myAct)
                builder1.setTitle(
                    NG_Application.applicationContext().getResources().getString(R.string.ng_app_name) + NG_Application.applicationContext().getResources().getString(
                        R.string.Perm_Dlg_Title
                    )
                )
                builder1.setMessage(message)
                builder1.setCancelable(true)

                builder1.setPositiveButton(
                    android.R.string.yes
                ) { dialog, id ->
                    ActivityCompat.requestPermissions(
                        myActivity, permissionsList.toTypedArray(),
                        NG_Utils.NG_Val_Calls.MY_PERMISSION_BOTH
                    )
                    dialog.cancel()
                }

                builder1.setNegativeButton(
                    android.R.string.cancel
                ) { dialog, id -> dialog.cancel() }

                val alert11 = builder1.create()
                alert11.show()
            } else {
                ActivityCompat.requestPermissions(
                    myAct, permissionsList.toTypedArray(),
                    NG_Utils.NG_Val_Calls.MY_PERMISSION_BOTH
                )
            }
        }
    }

    private fun addPermission(
        MyAct: Activity,
        permissionsList: MutableList<String>,
        permission: String
    ): Boolean {
        var bResult = true
        if (ActivityCompat.checkSelfPermission(
                MyAct,
                permission
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(permission)
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MyAct, permission))
                bResult = false
        }
        return bResult
    }

    fun CheckRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {

        if (grantResults.size > 0) {
            for (i in grantResults.indices) {
                if (permissions[i].equals(Manifest.permission.READ_CALL_LOG, ignoreCase = true)) {
                    iPermissionTelStatus = grantResults[i]
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(
                            NG_Application.applicationContext(),
                            R.string.Perm_Calls_Granted,
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            NG_Application.applicationContext(),
                            R.string.Perm_Calls_Denied,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                if (permissions[i].equals(Manifest.permission.READ_SMS, ignoreCase = true)) {
                    iPermissionSMSStatus = grantResults[i]
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(
                            NG_Application.applicationContext(),
                            R.string.Perm_SMS_Granted,
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            NG_Application.applicationContext(),
                            R.string.Perm_SMS_Denied,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}
