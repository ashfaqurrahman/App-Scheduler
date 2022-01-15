package com.airposted.appschedular.view.app_schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.airposted.appschedular.R
import com.airposted.appschedular.model.AppDetails
import com.airposted.appschedular.databinding.FragmentNewScheduleBinding
import com.airposted.appschedular.utils.Coroutines
import com.airposted.appschedular.utils.ScheduleWorker
import com.airposted.appschedular.utils.setProgressDialog
import com.airposted.appschedular.view.IOnBackPressed
import com.airposted.appschedular.view.home.CommunicatorFragmentInterface
import com.airposted.appschedular.view.home.HomeViewModelFactory
import com.airposted.appschedular.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NewScheduleFragment(private val appDetails: AppDetails?) : Fragment(R.layout.fragment_new_schedule), KodeinAware, IOnBackPressed {

    private lateinit var binding: FragmentNewScheduleBinding
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    var myCommunicator: CommunicatorFragmentInterface? = null

    var appDataSet = false
    var timeSet = false
    var appData: AppDetails? = null
    lateinit var c: Calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewScheduleBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity(), factory).get(HomeViewModel::class.java)

        bindUI()
        return binding.root
    }

    private fun bindUI() = Coroutines.main {
        myCommunicator = context as CommunicatorFragmentInterface

        if (appDetails != null) {
            binding.toolbar.toolbarTitle.text = "Update Schedule"

            try {
                val icon: Drawable =
                    requireActivity().packageManager.getApplicationIcon(appDetails.packageName!!)
                binding.icon.setImageDrawable(icon)
                binding.name.text = appDetails.name
                binding.packageName.text = appDetails.packageName
                binding.name.visibility = View.VISIBLE
                binding.packageName.visibility = View.VISIBLE
                appDataSet = true
                appData = appDetails
                c = Calendar.getInstance()
                val sdff = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
                c.time = sdff.parse(appDetails.time) // all done

                val myFormat = "MMM dd, yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
                binding.date.text = sdf.format(c.time)
                val timeFormat = "hh:mm a" // mention the format you need
                val ssdf = SimpleDateFormat(timeFormat, Locale.getDefault())
                binding.time.text = ssdf.format(c.time)
                timeSet = true
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            binding.save.text = "Update"
        } else {
            binding.toolbar.toolbarTitle.text = "New Schedule"
            c = Calendar.getInstance()
            val myFormat = "MMM dd, yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            binding.date.text = sdf.format(c.time)
        }

        viewModel.appData.observe(viewLifecycleOwner, {
            if (it.packageName != null) {
                try {
                    val icon: Drawable =
                        requireActivity().packageManager.getApplicationIcon(it.packageName!!)
                    binding.icon.setImageDrawable(icon)
                    binding.name.text = it.name
                    binding.packageName.text = it.packageName
                    binding.name.visibility = View.VISIBLE
                    binding.packageName.visibility = View.VISIBLE
                    appDataSet = true
                    appData = it
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
        })

        binding.appLayout.setOnClickListener {

            setProgressDialog(requireActivity())
            if (appDetails != null) {
                myCommunicator?.addContentFragment(AppListFragment(appDetails), true)
            } else {
                myCommunicator?.addContentFragment(AppListFragment(null), true)
            }
        }

        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
            viewModel.newAppData(AppDetails(null, null, null, null, null))
        }

        binding.datePick.setOnClickListener {

            if (timeSet) {
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(requireActivity(), { view, year, monthOfYear, dayOfMonth ->

                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "MMM dd, yyyy"
                    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
                    binding.date.text = sdf.format(c.time)

                }, year, month, day)
                dpd.datePicker.minDate = System.currentTimeMillis()
                dpd.show()
            } else {
                Toast.makeText(requireActivity(), "Set time first", Toast.LENGTH_LONG).show()
            }
        }

        binding.timePick.setOnClickListener {
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val tpd = TimePickerDialog(requireActivity(), { view, h, m ->

                c.set(Calendar.HOUR_OF_DAY, h)
                c.set(Calendar.MINUTE, m)
                c.set(Calendar.SECOND, 0)

                val timeFormat = "hh:mm a" // mention the format you need
                val ssdf = SimpleDateFormat(timeFormat, Locale.getDefault())
                binding.time.text = ssdf.format(c.time)
                timeSet = true

                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(requireActivity(), { view, year, monthOfYear, dayOfMonth ->

                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "MMM dd, yyyy"
                    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
                    binding.date.text = sdf.format(c.time)

                }, year, month, day)
                dpd.datePicker.minDate = System.currentTimeMillis()
                dpd.show()

            },hour,minute,false)

            tpd.show()
        }

        binding.save.setOnClickListener {

            if (appData != null) {
                setOneTimeWorkRequest(appData?.packageName!!)
            }else {
                Toast.makeText(requireActivity(), "No App found", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed(): Boolean {
        viewModel.newAppData(AppDetails(null, null, null, null, null))
        return true
    }

    private fun setOneTimeWorkRequest(packageName: String) {

        val workManager = WorkManager.getInstance(requireActivity())

        val data: Data = Data.Builder()
            .putString("packageName", packageName)
            .build()
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .build()

        val dif = c.timeInMillis - Calendar.getInstance().timeInMillis
        val uploadRequest = OneTimeWorkRequest.Builder(ScheduleWorker::class.java)
            .setInitialDelay(dif, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        if (appDetails != null) {
            val calender = Calendar.getInstance()
            if (c.timeInMillis > calender.timeInMillis) {
                val myFormat = "MMM dd, yyyy hh:mm a" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
                lifecycleScope.launch {
                    viewModel.updateAppData(
                        AppDetails(
                            appData?.name,
                            appData?.packageName,
                            sdf.format(c.time),
                            uploadRequest.id.toString(),
                            false,
                            appData?.id
                        )
                    )
                    workManager.cancelWorkById(UUID.fromString(appData?.description))
                    workManager.enqueue(uploadRequest)
                    requireActivity().onBackPressed()
                }
            }
        } else {
            if (appDataSet) {
                if (timeSet) {
                    val calender = Calendar.getInstance()
                    if (c.timeInMillis > calender.timeInMillis) {
                        val myFormat = "MMM dd, yyyy hh:mm a"
                        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
                        lifecycleScope.launch {
                            viewModel.saveNewSchedule(AppDetails(appData?.name, appData?.packageName, sdf.format(c.time), uploadRequest.id.toString(), false))
                            workManager.enqueue(uploadRequest)
                            requireActivity().onBackPressed()
                        }
                    } else {
                        Toast.makeText(requireActivity(), "You must set a future date and time", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(requireActivity(), "Set time first", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireActivity(), "No App found", Toast.LENGTH_LONG).show()
            }
        }

        workManager.getWorkInfoByIdLiveData(uploadRequest.id)
            .observe(viewLifecycleOwner, {

                when (it.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        val successOutputData = it.outputData
                        Log.e("aaaaa", successOutputData.toString())

                    }
                    WorkInfo.State.FAILED -> {
                        val failureOutputData = it.outputData

                    }
                }
            })
    }
}