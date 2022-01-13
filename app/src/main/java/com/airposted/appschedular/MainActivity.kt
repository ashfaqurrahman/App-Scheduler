package com.airposted.appschedular

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airposted.appschedular.databinding.ActivityMainBinding
import com.airposted.appschedular.view.IOnBackPressed
import com.airposted.appschedular.view.home.CommunicatorFragmentInterface
import com.airposted.appschedular.view.home.HomeFragment
import com.airposted.appschedular.view.home.HomeViewModelFactory
import com.airposted.appschedular.viewmodel.HomeViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), KodeinAware, CommunicatorFragmentInterface {
    private lateinit var mainBinding: ActivityMainBinding
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        addContentFragment(HomeFragment(), false)
    }

    override fun setContentFragment(fragment: Fragment?, addToBackStack: Boolean) {
        if (fragment == null) {
            return
        }
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.frameLayout)
        if (currentFragment != null && fragment.javaClass.isAssignableFrom(currentFragment.javaClass)) {
            return
        }
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment, fragment.javaClass.name)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.javaClass.name)
        }
        fragmentTransaction.commit()
        fragmentManager.executePendingTransactions()
    }

    override fun addContentFragment(fragment: Fragment?, addToBackStack: Boolean) {
        if (fragment == null) {
            return
        }
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.frameLayout)

        if (currentFragment != null && fragment.javaClass.isAssignableFrom(currentFragment.javaClass)) {
            return
        }

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, 0, 0, R.anim.slide_in_down)
        fragmentTransaction.add(R.id.frameLayout, fragment, fragment.javaClass.name)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.javaClass.name)
        }
        fragmentTransaction.commit()
        fragmentManager.executePendingTransactions()
    }

    override fun onBackPressed() {
        val fragment =
            this.supportFragmentManager.findFragmentById(R.id.frameLayout)
        (fragment as? IOnBackPressed)?.onBackPressed()?.not()?.let {
            super.onBackPressed()
        }
    }
}