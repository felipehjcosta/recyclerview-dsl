package com.github.felipehjcosta.recyclerviewdsl

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

internal class SimpleRecyclerViewAdapter(
        internal val adapterConfigurationMapping: AdapterConfigurationMapping
) : RecyclerView.Adapter<SimpleRecyclerViewAdapter.SimpleRecyclerViewHolder>() {

    override fun getItemCount(): Int = adapterConfigurationMapping.valueAt(0).adapterConfigurationData?.items?.size
            ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(adapterConfigurationMapping.keyAt(viewType), parent, false)
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

    fun update(newLayoutBinds: AdapterConfigurationMapping) {
        val key = newLayoutBinds.keyAt(0)
        val newValue = newLayoutBinds.valueAt(0)
        var currentValue = adapterConfigurationMapping.get(key)

        if (currentValue == null) {
            adapterConfigurationMapping.clear()
            adapterConfigurationMapping.append(key, newValue)
            currentValue = adapterConfigurationMapping.get(key)
        }
        newValue.adapterConfigurationData?.let {
            currentValue.adapterConfigurationData = it
            notifyDataSetChanged()
        }
        newValue.adapterConfigurationExtraData?.let {
            currentValue.adapterConfigurationData?.items?.let {
                val newItems = newValue.adapterConfigurationExtraData?.items
                if (newItems != null) {
                    val positionStart = it.size
                    val itemCount = newItems.size
                    it.addAll(newItems)
                    notifyItemRangeInserted(positionStart, itemCount)
                }
            }
        }
    }

    internal inner class SimpleRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view)
}