package com.example.xview_adcash_qa.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.xview_adcash_qa.data.Advertisement
import com.example.xview_adcash_qa.data.Application
import com.example.xview_adcash_qa.databinding.ItemAdvertisementBinding

data class SelectableAdvertisement(
    val advertisement: Advertisement,
    val isSelected: Boolean
)

class AdAdapter(private val onItemClicked: (Advertisement) -> Unit) :
    ListAdapter<SelectableAdvertisement, AdAdapter.AdViewHolder>(AdDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
        val binding = ItemAdvertisementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AdViewHolder(private val binding: ItemAdvertisementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClicked(getItem(adapterPosition).advertisement)
            }
        }

        fun bind(item: SelectableAdvertisement) {
            binding.textPid.text = "PID: ${item.advertisement.pid}"
            binding.textType.text = "Type: ${item.advertisement.type}"
            binding.checkboxSelected.isChecked = item.isSelected
        }
    }

    private class AdDiffCallback : DiffUtil.ItemCallback<SelectableAdvertisement>() {
        override fun areItemsTheSame(
            oldItem: SelectableAdvertisement,
            newItem: SelectableAdvertisement
        ): Boolean {
            return oldItem.advertisement.id == newItem.advertisement.id
        }

        override fun areContentsTheSame(
            oldItem: SelectableAdvertisement,
            newItem: SelectableAdvertisement
        ): Boolean {
            return oldItem == newItem
        }
    }
}