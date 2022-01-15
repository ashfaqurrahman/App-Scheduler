package com.airposted.appschedular.view.app_schedule

import com.airposted.appschedular.model.AppDetails

interface AppListClickListener {
    fun onItemClick(data: AppDetails)
}