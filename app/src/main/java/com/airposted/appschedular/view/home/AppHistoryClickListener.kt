package com.airposted.appschedular.view.home

import com.airposted.appschedular.model.AppDetails

interface AppHistoryClickListener {
    fun onItemClick(data: AppDetails)
}