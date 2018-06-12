package com.github.felipehjcosta.recyclerviewdsl

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

internal class SimpleRecyclerViewAdapter(
        private val items: List<Any?>,
        private val layoutBinds: SparseArray<RecyclerViewAdapterBindDslBuilder>
) : RecyclerView.Adapter<SimpleRecyclerViewAdapter.SimpleRecyclerView>() {

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleRecyclerView {
        val view = LayoutInflater.from(parent.context).inflate(layoutBinds.keyAt(0), parent, false)
        return SimpleRecyclerView(view)
    }

    override fun onBindViewHolder(holder: SimpleRecyclerView, position: Int) {
        val bindMap = layoutBinds.valueAt(0).bindMap
        val item = items[position]
        val id = bindMap.keyAt(0)
        val view = holder.itemView.findViewById<View>(id)
        val block = bindMap.valueAt(0)
        block(item, view)
    }

    internal class SimpleRecyclerView(view: View) : RecyclerView.ViewHolder(view)
}