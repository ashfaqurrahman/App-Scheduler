package com.airposted.appschedular.view.app_schedule

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.airposted.appschedular.R
import com.airposted.appschedular.model.AppDetails
import com.airposted.appschedular.databinding.FragmentAppListBinding
import com.airposted.appschedular.utils.Coroutines
import com.airposted.appschedular.utils.dismissDialog
import com.airposted.appschedular.utils.setProgressDialog
import com.airposted.appschedular.view.IOnBackPressed
import com.airposted.appschedular.view.home.CommunicatorFragmentInterface
import com.airposted.appschedular.view.home.HomeViewModelFactory
import com.airposted.appschedular.viewmodel.HomeViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class AppListFragment(private val data: AppDetails?) : Fragment(R.layout.fragment_app_list), KodeinAware, AppListClickListener, IOnBackPressed {

    private lateinit var binding: FragmentAppListBinding
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    var myCommunicator: CommunicatorFragmentInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity(), factory).get(HomeViewModel::class.java)

        bindUI()
        return binding.root
    }

    private fun bindUI() = Coroutines.main {

        binding.toolbar.toolbarTitle.text = "App List"

        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }

        installedApps()
    }

    private fun installedApps() {
        val list = requireActivity().packageManager.getInstalledPackages(0)
        val apps = ArrayList<AppDetails>()
        for (i in list.indices) {
            val packageInfo = list[i]
            val appPackageName = packageInfo.applicationInfo.packageName
            val appName = packageInfo.applicationInfo.loadLabel(requireActivity().packageManager)
            Log.e("aaaaa", appPackageName)
            Log.e("bbbbb", appName.toString())
            if (packageInfo!!.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                val appName = packageInfo.applicationInfo.loadLabel(requireActivity().packageManager)
                val appPackageName = packageInfo.applicationInfo.packageName
                apps.add(AppDetails(name = appName.toString(), packageName = appPackageName, null, null, null))
            }
        }
        initRecyclerView(apps)
    }

    private fun initRecyclerView(list: List<AppDetails>) {
        binding.apps.visibility = View.VISIBLE
        val myRecyclerViewAdapter = AppListRecyclerViewAdapter(
            list,
            requireActivity(),
            this@AppListFragment,
        )
        binding.apps.layoutManager = GridLayoutManager(
            requireActivity(),
            1
        )
        binding.apps.itemAnimator = DefaultItemAnimator()
        binding.apps.adapter = myRecyclerViewAdapter

        dismissDialog()
    }

    override fun onItemClick(data: AppDetails) {
        if (data != null) {
            viewModel.newAppData(AppDetails(data.name, data.packageName, data.time, data.description, null, data.id))
        } else {
            viewModel.newAppData(data)
        }
        requireActivity().onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        return true
    }
}