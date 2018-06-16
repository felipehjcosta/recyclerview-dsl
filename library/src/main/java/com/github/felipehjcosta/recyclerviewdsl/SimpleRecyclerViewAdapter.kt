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
        val bindMap = layoutBinds.valueAt(holder.itemViewType).bindMap
        val item = items[position]
        val id = bindMap.keyAt(0)
        val view = holder.itemView.findViewById<View>(id)
        val block = bindMap.valueAt(0)
        block(item, view)
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