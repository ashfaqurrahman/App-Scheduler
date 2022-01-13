package com.airposted.appschedular.viewmodel

import androidx.lifecycle.ViewModel
import com.airposted.appschedular.model.AppDetails
import com.airposted.appschedular.data.repositories.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    suspend fun allAppData() = withContext(Dispatchers.IO) { repository.getAllFromLocations() }
    suspend fun saveNewSchedule(data: AppDetails) = withContext(Dispatchers.IO) { repository.saveNewSchedule(data) }
    suspend fun updateAppData(data: AppDetails) = withContext(Dispatchers.IO) { repository.updateAppData(data) }
    suspend fun deleteAppData(data: AppDetails) = withContext(Dispatchers.IO) { repository.deleteAppData(data) }

    val newData = repository.newData()
    val appData = repository.appData()
    fun newAppData(data: AppDetails) = repository.newAppData(data)

}