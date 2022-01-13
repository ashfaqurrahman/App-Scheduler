package com.airposted.appschedular.view.home

import androidx.fragment.app.Fragment

interface CommunicatorFragmentInterface {
    fun addContentFragment(fragment: Fragment?, addToBackStack: Boolean)

    fun setContentFragment(fragment: Fragment?, addToBackStack: Boolean)
}