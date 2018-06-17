package com.github.felipehjcosta.recyclerviewdsl

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import kotlin.reflect.KClass

fun onRecyclerView(recyclerView: RecyclerView, block: RecyclerViewDslBuilder.() -> Unit) {
    val builder = RecyclerViewDslBuilder(recyclerView.context)
    block(builder)
    builder.layoutManager?.let { recyclerView.layoutManager = it }
    builder.recyclerViewAdapterDsl?.layoutBinds?.let {
        recyclerView.adapter = SimpleRecyclerViewAdapter.newInstance(
                builder.recyclerViewAdapterDsl?.items ?: emptyList(),
                it
        )
    }
}

class RecyclerViewDslBuilder(private val context: Context) {
    internal var layoutManager: RecyclerView.LayoutManager? = null

    internal var recyclerViewAdapterDsl: RecyclerViewAdapterDslBuilder<out Any>? = null

    fun withLinearLayout(block: LinearLayoutManager.() -> Unit) {
        layoutManager = LinearLayoutManager(context).apply(block)
    }

    fun <ITEM : Any> withTypedItems(items: List<ITEM?>,
                                    itemType: KClass<ITEM>,
                                    block: RecyclerViewAdapterDslBuilder<ITEM>.() -> Unit) {
        recyclerViewAdapterDsl = RecyclerViewAdapterDslBuilder(items, itemType).apply(block)
    }

    inline fun <reified ITEM : Any> withItems(items: List<ITEM?>, noinline block: RecyclerViewAdapterDslBuilder<ITEM>.() -> Unit) {
        withTypedItems(items, ITEM::class, block)
    }
}

class RecyclerViewAdapterDslBuilder<ITEM : Any>(
        internal val items: List<ITEM?>,
        private val itemType: KClass<ITEM>,
        internal val layoutBinds: SparseArray<RecyclerViewAdapterBindDslBuilder<ITEM>> = SparseArray()
) {

    fun bind(layoutResId: Int, block: RecyclerViewAdapterBindDslBuilder<ITEM>.() -> Unit) {
        layoutBinds.put(layoutResId, RecyclerViewAdapterBindDslBuilder(itemType).apply(block))
    }
}

class RecyclerViewAdapterBindDslBuilder<ITEM : Any>(
        internal val itemType: KClass<ITEM>
) {
    internal val bindMap = SparseArray<(item: Any?, view: View?) -> Unit>()
    internal var onClickBlock: (Int, ITEM) -> Unit = { i: Int, item: ITEM -> }

    fun <VIEW : View> append(id: Int,
                             block: (RecyclerViewAdapterBindItem<ITEM, VIEW>) -> Unit,
                             viewType: KClass<VIEW>
    ) {
        bindMap.append(id, RecyclerViewAdapterBindItemWrapper(block, itemType, viewType))
    }

    inline fun <reified VIEW : View> on(
            id: Int,
            noinline block: (RecyclerViewAdapterBindItem<ITEM, VIEW>) -> Unit) {
        append(id, block, VIEW::class)
    }

    fun onClick(onClickBlock: (Int, ITEM) -> Unit) {
        this.onClickBlock = onClickBlock
    }

    internal fun onLayoutViewClicked(position: Int, item: Any?) {
        onClickBlock(position, itemType.java.cast(item))
    }

}

class RecyclerViewAdapterBindItem<ITEM : Any, VIEW : Any>(
        val item: ITEM?,
        val view: VIEW?
)

class RecyclerViewAdapterBindItemWrapper<ITEM : Any, VIEW : View>(
        private val block: (RecyclerViewAdapterBindItem<ITEM, VIEW>) -> Unit,
        private val itemType: KClass<ITEM>,
        private val viewType: KClass<VIEW>
) : kotlin.Function2<Any?, View?, Unit> {

    override fun invoke(item: Any?, view: View?) {
        block(RecyclerViewAdapterBindItem(itemType.java.cast(item), viewType.java.cast(view)))
    }
}
