package com.github.felipehjcosta.recyclerviewdsl

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import kotlin.reflect.KClass

fun onRecyclerView(recyclerView: RecyclerView, block: RecyclerViewDslBuilder.() -> Unit) {
    val builder = RecyclerViewDslBuilder(recyclerView.context)
    block(builder)
    builder.layoutManager?.let { recyclerView.layoutManager = it }

    val adapter = recyclerView.adapter as? SimpleRecyclerViewAdapter?
    if (adapter != null) {
        adapter.update(builder.layoutBinds)
    } else {
        recyclerView.adapter = SimpleRecyclerViewAdapter.newInstance(builder.layoutBinds)
    }
}

class RecyclerViewDslBuilder(private val context: Context) {
    internal var layoutManager: RecyclerView.LayoutManager? = null

    internal val layoutBinds: SparseArray<RecyclerViewAdapterBuilder> = SparseArray()

    fun withLinearLayout(block: LinearLayoutManager.() -> Unit) {
        layoutManager = LinearLayoutManager(context).apply(block)
    }

    fun withGridLayout(block: GridLayoutManager.() -> Unit) {
        layoutManager = GridLayoutManager(context, 1).apply(block)
    }

    fun <LAYOUTMANAGER : RecyclerView.LayoutManager> withLayout(layoutManager: LAYOUTMANAGER) {
        this.layoutManager = layoutManager
    }

    fun <LAYOUTMANAGER : RecyclerView.LayoutManager> withLayout(
            layoutManager: LAYOUTMANAGER,
            block: LAYOUTMANAGER.() -> Unit
    ) {
        this.layoutManager = layoutManager.apply(block)
    }

    fun bind(layoutResId: Int, block: RecyclerViewAdapterBuilder.() -> Unit) {
        layoutBinds.put(layoutResId, RecyclerViewAdapterBuilder().apply(block))
    }
}

class RecyclerViewAdapterBuilder {

    internal var recyclerViewAdapterDsl: RecyclerViewAdapterDataBuilder<out Any>? = null

    internal var extraDataBuilder: RecyclerViewAdapterExtraDataBuilder<out Any>? = null

    fun <ITEM : Any> withTypedItems(items: List<Any?>,
                                    itemType: KClass<ITEM>,
                                    block: RecyclerViewAdapterDataBuilder<ITEM>.() -> Unit) {
        recyclerViewAdapterDsl = RecyclerViewAdapterDataBuilder(items.toMutableList(), itemType).apply(block)
    }

    inline fun <reified ITEM : Any> withItems(items: List<ITEM?>, noinline block: RecyclerViewAdapterDataBuilder<ITEM>.() -> Unit) {
        withTypedItems(items, ITEM::class, block)
    }

    fun <ITEM : Any> addExtraItems(items: List<ITEM?>) {
        extraDataBuilder = RecyclerViewAdapterExtraDataBuilder(items.toList())
    }
}

class RecyclerViewAdapterDataBuilder<ITEM : Any>(
        internal var items: MutableList<Any?>,
        private val itemType: KClass<ITEM>
) {

    internal val bindMap = SparseArray<((item: Any?, view: View?) -> Unit)?>()
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

class RecyclerViewAdapterExtraDataBuilder<ITEM : Any>(internal val items: List<ITEM?>)

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
