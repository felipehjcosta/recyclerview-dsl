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
    builder.recyclerViewAdapterDsl?.bindDsl?.let {
        recyclerView.adapter = SimpleRecyclerViewAdapter(
                builder.recyclerViewAdapterDsl?.items ?: emptyList(),
                it.layoutResId,
                it.bindMap
        )
    }
}

class RecyclerViewDslBuilder(private val context: Context) {
    internal var layoutManager: RecyclerView.LayoutManager? = null

    internal var recyclerViewAdapterDsl: RecyclerViewAdapterDslBuilder? = null

    fun withLinearLayout(block: LinearLayoutManager.() -> Unit) {
        layoutManager = LinearLayoutManager(context).apply(block)
    }

    fun withItems(items: List<Any?>, block: RecyclerViewAdapterDslBuilder.() -> Unit) {
        recyclerViewAdapterDsl = RecyclerViewAdapterDslBuilder(items).apply(block)
    }
}

class RecyclerViewAdapterDslBuilder(internal val items: List<Any?>) {

    internal var bindDsl: RecyclerViewAdapterBindDslBuilder? = null

    fun bind(layoutResId: Int, block: RecyclerViewAdapterBindDslBuilder.() -> Unit) {
        bindDsl = RecyclerViewAdapterBindDslBuilder(layoutResId).apply(block)
    }
}

class RecyclerViewAdapterBindDslBuilder(internal val layoutResId: Int) {
    internal val bindMap = SparseArray<(item: Any?, view: View?) -> Unit>()

    fun <ITEM : Any, VIEW : View> append(id: Int,
                                         block: (RecyclerViewAdapterBindItem<ITEM, VIEW>) -> Unit,
                                         itemType: KClass<ITEM>,
                                         viewType: KClass<VIEW>
    ) {
        bindMap.append(id, RecyclerViewAdapterBindItemWrapper(block, itemType, viewType))
    }

    inline fun <reified ITEM : Any, reified VIEW : View> on(
            id: Int,
            noinline block: (RecyclerViewAdapterBindItem<ITEM, VIEW>) -> Unit) {
        append(id, block, ITEM::class, VIEW::class)
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
