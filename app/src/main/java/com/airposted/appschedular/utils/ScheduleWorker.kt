package com.airposted.appschedular.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ScheduleWorker(context: Context, params:WorkerParameters) : Worker(context,params) {
    companion object{
        const val KEY_WORKER = "key_worker"
    }

    override fun doWork(): Result {
        return try {

            val intent: Intent =
                applicationContext.packageManager.getLaunchIntentForPackage(inputData.getString("packageName")!!)!!
            if (intent != null) {
                applicationContext.startActivity(intent)
            } else {
                Toast.makeText(
                    applicationContext,
                    inputData.getString("packageName")!! + " Error, Please Try Again.",
                    Toast.LENGTH_LONG
                ).show()
            }

            val time = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = time.format(Date())

            val outPutData = Data.Builder()
                .putString(KEY_WORKER,currentDate)
                .build()

            return Result.success(outPutData)
        } catch (e:Exception){
            Result.failure()
        }
    }
}