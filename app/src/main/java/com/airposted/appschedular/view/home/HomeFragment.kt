package com.airposted.appschedular.view.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.WorkManager
import com.airposted.appschedular.R
import com.airposted.appschedular.model.AppDetails
import com.airposted.appschedular.databinding.FragmentHomeBinding
import com.airposted.appschedular.utils.Coroutines
import com.airposted.appschedular.view.IOnBackPressed
import com.airposted.appschedular.view.app_schedule.NewScheduleFragment
import com.airposted.appschedular.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

import android.content.Intent
import android.net.Uri

import android.os.Build
import android.provider.Settings
import android.widget.Toast

import android.annotation.TargetApi





class HomeFragment : Fragment(R.layout.fragment_home), KodeinAware, AppHistoryClickListener, DeleteClickListener, IOnBackPressed {

    private lateinit var binding: FragmentHomeBinding
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    var myCommunicator: CommunicatorFragmentInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity(), factory).get(HomeViewModel::class.java)

        bindUI()
        return binding.root
    }

    private fun bindUI() = Coroutines.main {
        checkStartPermissionRequest()
        binding.toolbar.backImage.visibility = View.GONE
        binding.toolbar.toolbarTitle.text = requireActivity().applicationInfo.loadLabel(requireActivity().packageManager)
        myCommunicator = context as CommunicatorFragmentInterface
        lifecycleScope.launch {
            val allAppData = viewModel.allAppData()
            if (allAppData.isNotEmpty()) {
                initRecyclerView(allAppData)
                binding.orders.visibility = View.VISIBLE
                binding.noOrder.visibility = View.GONE
            } else {
                binding.orders.visibility = View.GONE
                binding.noOrder.visibility = View.VISIBLE
            }
        }

        viewModel.newData.observe(viewLifecycleOwner, {
            if (it) {
                lifecycleScope.launch {
                    val allAppData = viewModel.allAppData()
                    if (allAppData.isNotEmpty()) {
                        initRecyclerView(allAppData)
                        binding.orders.visibility = View.VISIBLE
                        binding.noOrder.visibility = View.GONE
                    } else {
                        binding.orders.visibility = View.GONE
                        binding.noOrder.visibility = View.VISIBLE
                    }
                }
            }
        })

        binding.add.setOnClickListener {
            myCommunicator?.addContentFragment(NewScheduleFragment(null), true)
        }
    }

    private fun initRecyclerView(list: List<AppDetails>) {
        binding.orders.visibility = View.VISIBLE
        val myRecyclerViewAdapter = SavedAppListRecyclerViewAdapter(
            list,
            requireActivity(),
            this@HomeFragment,
            this@HomeFragment,
        )
        binding.orders.layoutManager = GridLayoutManager(
            requireActivity(),
            1
        )
        binding.orders.itemAnimator = DefaultItemAnimator()
        binding.orders.adapter = myRecyclerViewAdapter
    }

    override fun onItemClick(data: AppDetails) {
        myCommunicator?.addContentFragment(NewScheduleFragment(data), true)
    }

    override fun onBackPressed(): Boolean {
        return true
    }

    override fun onDeleteClick(data: AppDetails) {
        lifecycleScope.launch {
            viewModel.deleteAppData(data)

            val workManager = WorkManager.getInstance(requireActivity())
            workManager.cancelWorkById(UUID.fromString(data.description))
        }
    }

    private fun checkStartPermissionRequest(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(requireActivity())) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + requireActivity().packageName)
                )
                startActivityForResult(intent, 10)
                return false // above will start new Activity with proper app setting
            }
        }
        return true // on lower OS versions granted during apk installation
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 10) {
            if (Settings.canDrawOverlays(requireActivity())) {
                if (!checkStartPermissionRequest()) {
                    checkStartPermissionRequest()
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Permission Granted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}