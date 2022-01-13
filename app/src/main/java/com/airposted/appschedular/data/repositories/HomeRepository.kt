package com.airposted.appschedular.data.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airposted.appschedular.data.db.AppDatabase
import com.airposted.appschedular.model.AppDetails

class HomeRepository(
    context: Context,
    private val db: AppDatabase
) {
    private val newData = MutableLiveData<Boolean>()
    private val appData = MutableLiveData<AppDetails>()

    fun getAllFromLocations() =
        db.getRunDao().getAllAppData()

    suspend fun saveNewSchedule(data: AppDetails) {
        db.getRunDao().insertAppData(data)
        newData.postValue(true)
    }

    fun updateAppData(data: AppDetails) {
        db.getRunDao().updateAppData(data)
        newData.postValue(true)
    }

    suspend fun deleteAppData(data: AppDetails) {
        db.getRunDao().deleteAppData(data)
        newData.postValue(true)
    }

    fun newAppData(data: AppDetails) {
        appData.postValue(data)
    }

    fun newData(): LiveData<Boolean> {
        return newData
    }

    fun appData(): LiveData<AppDetails> {
        return appData
    }
}