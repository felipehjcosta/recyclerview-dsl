package com.github.felipecosta.recyclerviewdsl.sample.objects

object DummyContent {

    val ITEMS: MutableList<DummyItem> = mutableListOf()

    private const val COUNT = 25

    init {
        for (i in 1..COUNT) {
            addItem(createDummyItem(i))
        }
    }

    private fun addItem(item: DummyItem) {
        ITEMS.add(item)
    }

    private fun createDummyItem(position: Int): DummyItem {
        return DummyItem(position.toString(), "Item $position", makeDetails(position))
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0 until position) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    data class DummyItem(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}
