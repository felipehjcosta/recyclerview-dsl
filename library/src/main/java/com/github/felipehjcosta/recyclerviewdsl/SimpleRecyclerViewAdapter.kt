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

    fun update(key: Int, adapterConfigurationData: AdapterConfigurationData<out Any>) {
        adapterConfigurationMapping.get(key).adapterConfigurationData = adapterConfigurationData
        notifyDataSetChanged()
    }

    fun addExtra(key: Int, adapterConfigurationExtraData: AdapterConfigurationExtraData<out Any>) {
        adapterConfigurationMapping.get(key).adapterConfigurationData?.items?.let {
            val newItems = adapterConfigurationExtraData.items
            val positionStart = it.size
            val itemCount = newItems.size
            it.addAll(newItems)
            notifyItemRangeInserted(positionStart, itemCount)
        }
    }

    internal inner class SimpleRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view)
}