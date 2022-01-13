package com.airposted.appschedular.view.app_schedule

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
import com.airposted.appschedular.databinding.SavedAppListItemBinding

class AppListRecyclerViewAdapter(
    private val dataModelList: List<AppDetails>,
    private val context: Context,
    private val appHistoryClickListener: AppListClickListener
) : RecyclerView.Adapter<AppListRecyclerViewAdapter.ViewHolder>() {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = dataModelList[position]
        holder.bind(dataModel)

        holder.binding.delete.setImageDrawable(context.getDrawable(android.R.drawable.ic_input_add))

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