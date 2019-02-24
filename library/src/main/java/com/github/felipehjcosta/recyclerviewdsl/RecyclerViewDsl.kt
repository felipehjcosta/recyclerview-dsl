package com.github.felipehjcosta.recyclerviewdsl

import android.content.Context
import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KClass

typealias AdapterConfigurationMapping = SparseArray<AdapterConfiguration>
typealias AdapterItemBinderMapping = SparseArray<AdapterItemBinder?>

fun onRecyclerView(recyclerView: RecyclerView, block: RecyclerViewConfiguration.() -> Unit) {
    RecyclerViewConfiguration(recyclerView.context).apply {
        block(this)
        layoutManager?.let { recyclerView.layoutManager = it }
        adapterConfigurationMapping?.let { handleNewAdapterConfigurationMapping(recyclerView, it) }
    }
}

private fun handleNewAdapterConfigurationMapping(
    recyclerView: RecyclerView,
    newAdapterConfigurationMapping: AdapterConfigurationMapping
) {
    val currentAdapter = recyclerView.adapter
    when (currentAdapter) {
        is SimpleRecyclerViewAdapter -> {
            if (isContentEquals(currentAdapter, newAdapterConfigurationMapping)) {
                updateAdapter(currentAdapter, newAdapterConfigurationMapping)
            } else {
                assignNewAdapter(recyclerView, newAdapterConfigurationMapping)
            }
        }
        else -> assignNewAdapter(recyclerView, newAdapterConfigurationMapping)
    }
}

private fun assignNewAdapter(
    recyclerView: RecyclerView,
    newAdapterConfigurationMapping: AdapterConfigurationMapping
) {
    recyclerView.adapter = SimpleRecyclerViewAdapter(newAdapterConfigurationMapping)
}

private fun isContentEquals(
    adapter: SimpleRecyclerViewAdapter,
    newAdapterConfigurationMapping: AdapterConfigurationMapping
): Boolean {
    val currentAdapterConfigurationMapping = adapter.adapterConfigurationMapping

    val newKeys = IntArray(newAdapterConfigurationMapping.size()) {
        newAdapterConfigurationMapping.keyAt(it)
    }
    val currentKeys = IntArray(currentAdapterConfigurationMapping.size()) {
        currentAdapterConfigurationMapping.keyAt(it)
    }
    return newKeys.contentEquals(currentKeys)
}

private fun updateAdapter(
    adapter: SimpleRecyclerViewAdapter,
    newAdapterConfigurationMapping: AdapterConfigurationMapping
) {
    adapter.adapterConfigurationMapping
        .run { IntArray(size()) { adapter.adapterConfigurationMapping.keyAt(it) } }
        .forEach { key ->
            newAdapterConfigurationMapping.get(key)
                ?.adapterConfigurationData
                ?.let { adapter.update(key, it) }

            newAdapterConfigurationMapping.get(key)
                ?.adapterConfigurationExtraData
                ?.let { adapter.addExtra(key, it) }
        }
}

class RecyclerViewConfiguration(private val context: Context) {
    internal var layoutManager: RecyclerView.LayoutManager? = null

    internal var adapterConfigurationMapping: AdapterConfigurationMapping? = null

    fun withLinearLayout(block: LinearLayoutManager.() -> Unit = {}) {
        layoutManager = LinearLayoutManager(context).apply(block)
    }

    fun withGridLayout(spanCount: Int = 1, block: GridLayoutManager.() -> Unit = {}) {
        layoutManager = GridLayoutManager(context, spanCount).apply(block)
    }

    fun <LAYOUT : RecyclerView.LayoutManager> withLayout(layoutManager: LAYOUT) {
        this.layoutManager = layoutManager
    }

    fun <LAYOUT : RecyclerView.LayoutManager> withLayout(
        layoutManager: LAYOUT,
        block: LAYOUT.() -> Unit = {}
    ) {
        this.layoutManager = layoutManager.apply(block)
    }

    fun bind(layoutResId: Int, block: AdapterConfiguration.() -> Unit) {
        if (adapterConfigurationMapping == null) {
            adapterConfigurationMapping = AdapterConfigurationMapping()
        }
        adapterConfigurationMapping?.put(layoutResId, AdapterConfiguration().apply(block))
    }
}

data class AdapterConfiguration(
    internal var adapterConfigurationData: AdapterConfigurationData<out Any>? = null,
    internal var adapterConfigurationExtraData: AdapterConfigurationExtraData<out Any>? = null
) {

    fun <ITEM : Any> withTypedItems(
        items: List<Any?>,
        itemType: KClass<ITEM>,
        block: AdapterConfigurationData<ITEM>.() -> Unit
    ) {
        adapterConfigurationData =
            AdapterConfigurationData(items.toMutableList(), itemType).apply(block)
    }

    inline fun <reified ITEM : Any> withItems(
        items: List<ITEM?>,
        noinline block: AdapterConfigurationData<ITEM>.() -> Unit
    ) {
        withTypedItems(items, ITEM::class, block)
    }

    fun <ITEM : Any> addExtraItems(items: List<ITEM?>) {
        adapterConfigurationExtraData = AdapterConfigurationExtraData(items.toList())
    }
}

data class AdapterConfigurationData<ITEM : Any>(
    internal var items: MutableList<Any?>,
    private val itemType: KClass<ITEM>
) {

    internal val adapterItemBinderMapping = AdapterItemBinderMapping()
    private var onItemClickListener: OnItemClickListener<ITEM> =
        object : OnItemClickListener<ITEM> {
            override fun onItemClick(position: Int, item: ITEM?) {

            }
        }

    fun <VIEW : View> append(
        id: Int,
        viewType: KClass<VIEW>,
        block: (AdapterItemBind<ITEM, VIEW>) -> Unit
    ) {
        adapterItemBinderMapping.append(id, TypeAdapterItemBinder(block, itemType, viewType))
    }

    inline fun <reified VIEW : View> on(
        id: Int,
        noinline block: (AdapterItemBind<ITEM, VIEW>) -> Unit
    ) {
        append(id, VIEW::class, block)
    }

    fun onClick(onClickBlock: OnItemClickListener<ITEM>) {
        this.onItemClickListener = onClickBlock
    }

    fun onClick(onClickBlock: (Int, ITEM?) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener<ITEM> {
            override fun onItemClick(position: Int, item: ITEM?) {
                onClickBlock(position, item)
            }
        }
    }

    internal fun onLayoutViewClicked(position: Int, item: Any?) {
        onItemClickListener.onItemClick(position, itemType.java.cast(item))
    }

}

data class AdapterConfigurationExtraData<ITEM : Any>(internal val items: List<ITEM?>)

data class AdapterItemBind<ITEM : Any, VIEW : View>(val item: ITEM?, val view: VIEW?)

interface AdapterItemBinder {
    fun bind(item: Any?, view: View?)
}

data class TypeAdapterItemBinder<ITEM : Any, VIEW : View>(
    private val block: (AdapterItemBind<ITEM, VIEW>) -> Unit,
    private val itemType: KClass<ITEM>,
    private val viewType: KClass<VIEW>
) : AdapterItemBinder {
    override fun bind(item: Any?, view: View?) {
        block(AdapterItemBind(itemType.java.cast(item), viewType.java.cast(view)))
    }
}

interface OnItemClickListener<ITEM : Any> {
    fun onItemClick(position: Int, item: ITEM?)
}
