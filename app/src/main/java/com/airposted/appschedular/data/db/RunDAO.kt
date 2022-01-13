package com.airposted.appschedular.data.db

import androidx.room.*
import com.airposted.appschedular.model.AppDetails

@Dao
interface RunDAO {

    // AppDetails...
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppData(appData: AppDetails)

    @Delete
    suspend fun deleteAppData(appData: AppDetails)

    @Query("SELECT * FROM app_details ORDER BY time ASC")
    fun getAllAppData(): List<AppDetails>

    @Update(entity = AppDetails::class)
    fun updateAppData(obj: AppDetails)

//    @Query("UPDATE app_details SET name = :name, packageName = :packageName, time = :time WHERE id = :id")
//    fun updateAppData(id: Int, name: String, packageName: String, time: String)
}










