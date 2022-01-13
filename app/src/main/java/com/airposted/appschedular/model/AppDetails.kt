package com.airposted.appschedular.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_details")
data class AppDetails(
    var name: String? = null,
    var packageName:String? = null,
    var time:String? = null,
    var description:String? = null,
    var completed:Boolean? = null,
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
)