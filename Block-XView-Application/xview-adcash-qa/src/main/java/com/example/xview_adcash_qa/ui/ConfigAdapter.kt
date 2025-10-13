package com.example.xview_adcash_qa.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.xview_adcash_qa.data.Application
import com.example.xview_adcash_qa.databinding.ItemConfigBinding

data class SelectableApplication(
    val application: Application,
    val isSelected: Boolean
)

class ConfigAdapter(private val onItemClicked: (Application) -> Unit) :
    ListAdapter<SelectableApplication, ConfigAdapter.ConfigViewHolder>(ConfigDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfigViewHolder {
        val binding = ItemConfigBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConfigViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConfigViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ConfigViewHolder(private val binding: ItemConfigBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClicked(getItem(adapterPosition).application)
            }
        }

        fun bind(item: SelectableApplication) {
            binding.textName.text = item.application.name
            binding.textAppId.text = "AppID: ${item.application.appId}"
            binding.textEnv.text = "Env: ${item.application.environment}"
            binding.checkboxSelected.isChecked = item.isSelected
        }
    }

    private class ConfigDiffCallback : DiffUtil.ItemCallback<SelectableApplication>() {
        override fun areItemsTheSame(
            oldItem: SelectableApplication,
            newItem: SelectableApplication
        ): Boolean {
            // 아이템의 고유 ID로 같은 아이템인지 비교
            return oldItem.application.id == newItem.application.id
        }

        override fun areContentsTheSame(
            oldItem: SelectableApplication,
            newItem: SelectableApplication
        ): Boolean {
            // isSelected 상태까지 포함하여 내용이 완전히 같은지 비교
            return oldItem == newItem
        }
    }
}