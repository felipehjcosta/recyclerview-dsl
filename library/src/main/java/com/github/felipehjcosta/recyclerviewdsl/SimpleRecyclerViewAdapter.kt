package com.github.felipehjcosta.recyclerviewdsl

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

internal class SimpleRecyclerViewAdapter private constructor(
        private val layoutBinds: SparseArray<RecyclerViewAdapterBuilder>
) : RecyclerView.Adapter<SimpleRecyclerViewAdapter.SimpleRecyclerView>() {

    override fun getItemCount(): Int = layoutBinds.valueAt(0).recyclerViewAdapterDsl?.items?.size
            ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleRecyclerView {
        val view = LayoutInflater.from(parent.context).inflate(layoutBinds.keyAt(viewType), parent, false)
        return SimpleRecyclerView(view)
    }

    override fun onBindViewHolder(holder: SimpleRecyclerView, position: Int) {
        val binder = layoutBinds.valueAt(holder.itemViewType)

        binder.recyclerViewAdapterDsl?.let {
            val bindMap = it.bindMap
            val item = it.items[position]
            for (i in 0 until bindMap.size()) {
                val id = bindMap.keyAt(i)
                val view = holder.itemView.findViewById<View>(id)
                val block = bindMap.valueAt(i)
                block?.invoke(item, view)
            }
            holder.itemView.setOnClickListener { _ ->
                it.onLayoutViewClicked(position, item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    fun update(newLayoutBinds: SparseArray<RecyclerViewAdapterBuilder>) {
        for (i in 0 until newLayoutBinds.size()) {
            val key = newLayoutBinds.keyAt(0)
            val newValue = newLayoutBinds.valueAt(0)
            val currentValue = layoutBinds.get(key)
            if (currentValue == null) {
                layoutBinds.append(key, newValue)
            } else {
                newValue.recyclerViewAdapterDsl?.let {
                    currentValue.recyclerViewAdapterDsl = it
                    notifyDataSetChanged()
                }
                newValue.extraDataBuilder?.let {
                    currentValue.recyclerViewAdapterDsl?.items?.let {
                        val newItems = newValue.extraDataBuilder?.items
                        if (newItems != null) {
                            it.addAll(newItems)
                        }
                        val positionStart = it.size
                        notifyItemRangeInserted(positionStart, it.size)
                    }
                }
            }
        }
    }

    internal class SimpleRecyclerView(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        fun newInstance(layoutBinds: SparseArray<RecyclerViewAdapterBuilder>): SimpleRecyclerViewAdapter {
            return SimpleRecyclerViewAdapter(layoutBinds)
        }
    }
}