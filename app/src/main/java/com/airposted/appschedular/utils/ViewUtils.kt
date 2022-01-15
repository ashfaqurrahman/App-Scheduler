package com.airposted.appschedular.utils

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.airposted.appschedular.R
import com.google.android.material.snackbar.Snackbar

fun Context.toast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun ProgressBar.show(){
    visibility = View.VISIBLE
}

fun ProgressBar.hide(){
    visibility = View.GONE
}

fun View.snackbar(message: String){
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

var pDialog: ProgressDialog? = null

fun setProgressDialog(context: Context) {
    try {
        pDialog = ProgressDialog(context)
        pDialog!!.setCancelable(false)
        pDialog!!.setCanceledOnTouchOutside(false)
        pDialog!!.show()
        pDialog!!.setContentView(R.layout.custom_progress_bar)
        pDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pDialog!!.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    } catch (ignored: Exception) {
    }
}

fun dismissDialog() {
    try {
        if (pDialog != null && pDialog!!.isShowing) {
            pDialog!!.dismiss()
        }
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        pDialog = null
    }
}