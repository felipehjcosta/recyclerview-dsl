package com.github.felipehjcosta.recyclerviewdsl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

internal class SimpleRecyclerViewAdapter(
    internal val adapterConfigurationMapping: AdapterConfigurationMapping
) : RecyclerView.Adapter<SimpleRecyclerViewAdapter.SimpleRecyclerViewHolder>() {

    override fun getItemCount(): Int =
        adapterConfigurationMapping.valueAt(0).adapterConfigurationData?.items?.size
            ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(adapterConfigurationMapping.keyAt(viewType), parent, false)
        return SimpleRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: SimpleRecyclerViewHolder, position: Int) {
        val binder = adapterConfigurationMapping.valueAt(holder.itemViewType)

        binder.adapterConfigurationData?.let {
            val bindMap = it.adapterItemBinderMapping
            val item = it.items[position]
            for (i in 0 until bindMap.size()) {
                val id = bindMap.keyAt(i)
                val view = holder.itemView.findViewById<View>(id)
                val block = bindMap.valueAt(i)
                block?.bind(item, view)
            }
            holder.itemView.setOnClickListener { _ ->
                it.onLayoutViewClicked(position, item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    fun update(key: Int, adapterConfigurationData: AdapterConfigurationData<out Any>) {
        val oldList = adapterConfigurationMapping.get(key).adapterConfigurationData?.items?.toList()
            ?: emptyList()
        val newList = adapterConfigurationData.items.toList()
        val result = DiffUtil.calculateDiff(AnyDiffCallback(oldList = oldList, newList = newList))
        adapterConfigurationMapping.get(key).adapterConfigurationData = adapterConfigurationData
        result.dispatchUpdatesTo(this)
    }

    fun addExtra(key: Int, adapterConfigurationExtraData: AdapterConfigurationExtraData<out Any>) {
        val oldList = adapterConfigurationMapping.get(key).adapterConfigurationData?.items?.toList()
            ?: emptyList()
        val newList = adapterConfigurationMapping.get(key)
            .adapterConfigurationData
            ?.items
            ?.apply { addAll(adapterConfigurationExtraData.items) }?.toList() ?: emptyList()
        val result = DiffUtil.calculateDiff(AnyDiffCallback(oldList = oldList, newList = newList))
        result.dispatchUpdatesTo(this)
    }

    internal inner class SimpleRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view)
}