package com.airposted.appschedular.view.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.airposted.appschedular.BR
import com.airposted.appschedular.R
import com.airposted.appschedular.model.AppDetails
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.airposted.appschedular.databinding.SavedAppListItemBinding
import java.text.SimpleDateFormat
import java.util.*

class SavedAppListRecyclerViewAdapter(
    private val dataModelList: List<AppDetails>,
    private val context: Context,
    private val appHistoryClickListener: AppHistoryClickListener,
    private val deleteClickListener: DeleteClickListener
) : RecyclerView.Adapter<SavedAppListRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: SavedAppListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.saved_app_list_item, parent, false
        )
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = dataModelList[position]
        holder.bind(dataModel)

        holder.binding.time.visibility = View.VISIBLE

        try {
            val icon: Drawable =
                context.packageManager.getApplicationIcon(dataModel.packageName!!)
            holder.binding.icon.setImageDrawable(icon)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        holder.binding.layout.setOnClickListener {
            appHistoryClickListener.onItemClick(dataModel)
        }

        holder.binding.delete.setOnClickListener {
            deleteClickListener.onDeleteClick(dataModel)
        }

        val c = Calendar.getInstance()
        val sdff = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
        c.time = sdff.parse(dataModel.time)

        if (Calendar.getInstance().timeInMillis > c.timeInMillis) {
            holder.binding.time.setTextColor(context.getColor(android.R.color.darker_gray))
            holder.binding.layout.setCardBackgroundColor(context.getColor(R.color.grey))
        }

    }

    override fun getItemCount(): Int {
        return dataModelList.size
    }

    inner class ViewHolder(var binding: SavedAppListItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(obj: Any?) {
            binding.setVariable(BR.myArea, obj)
            binding.executePendingBindings()
        }
    }
}