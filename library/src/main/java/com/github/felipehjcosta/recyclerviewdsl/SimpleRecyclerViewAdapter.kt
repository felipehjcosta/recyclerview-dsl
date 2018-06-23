package com.github.felipehjcosta.recyclerviewdsl

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

internal class SimpleRecyclerViewAdapter private constructor(
        private val items: List<Any?>,
        private val layoutBinds: SparseArray<RecyclerViewAdapterBindDslBuilder<out Any>>
) : RecyclerView.Adapter<SimpleRecyclerViewAdapter.SimpleRecyclerView>() {

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleRecyclerView {
        val view = LayoutInflater.from(parent.context).inflate(layoutBinds.keyAt(viewType), parent, false)
        return SimpleRecyclerView(view)
    }

    override fun onBindViewHolder(holder: SimpleRecyclerView, position: Int) {
        val binder = layoutBinds.valueAt(holder.itemViewType)
        val bindMap = binder.bindMap
        val item = items[position]
        for (i in 0 until bindMap.size()) {
            val id = bindMap.keyAt(i)
            val view = holder.itemView.findViewById<View>(id)
            val block = bindMap.valueAt(i)
            block?.invoke(item, view)
        }
        holder.itemView.setOnClickListener {
            binder.onLayoutViewClicked(position, item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    internal class SimpleRecyclerView(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        fun newInstance(items: List<Any?>,
                        layoutBinds: SparseArray<out RecyclerViewAdapterBindDslBuilder<out Any>>): SimpleRecyclerViewAdapter {
            val layoutBindsWithoutOut = SparseArray<RecyclerViewAdapterBindDslBuilder<out Any>>().apply {
                for (i in 0 until layoutBinds.size()) {
                    val key = layoutBinds.keyAt(i)
                    val obj = layoutBinds.get(key)
                    put(key, obj)
                }
            }
            return SimpleRecyclerViewAdapter(items, layoutBindsWithoutOut)
        }
    }
}