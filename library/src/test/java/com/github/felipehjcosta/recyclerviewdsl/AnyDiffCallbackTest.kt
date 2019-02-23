package com.github.felipehjcosta.recyclerviewdsl

import org.junit.Assert.*
import org.junit.Test

class AnyDiffCallbackTest {

    @Test
    fun ensureDiffCallbackWorksWellWithSameItemsWithSameContent() {
        val firstItem = TestObject("a")
        val secondItem = TestObject("b")
        val thirdItems = TestObject("c")
        val diffCallbackWithSameItems = AnyDiffCallback(
            oldList = listOf(firstItem, secondItem),
            newList = listOf(firstItem, secondItem, thirdItems)
        )

        assertEquals(2, diffCallbackWithSameItems.oldListSize)
        assertEquals(3, diffCallbackWithSameItems.newListSize)
        assertTrue(diffCallbackWithSameItems.areItemsTheSame(0, 0))
        assertFalse(diffCallbackWithSameItems.areItemsTheSame(0, 1))
        assertTrue(diffCallbackWithSameItems.areContentsTheSame(0, 0))
        assertFalse(diffCallbackWithSameItems.areContentsTheSame(0, 1))
    }

    @Test
    fun ensureDiffCallbackWorksWellWithDifferentItemsWithSameContent() {
        val diffCallbackWithSameItems = AnyDiffCallback(
            oldList = listOf(TestObject("a"), TestObject("b")),
            newList = listOf(TestObject("a"), TestObject("b"), TestObject("c"))
        )

        assertEquals(2, diffCallbackWithSameItems.oldListSize)
        assertEquals(3, diffCallbackWithSameItems.newListSize)
        assertFalse(diffCallbackWithSameItems.areItemsTheSame(0, 0))
        assertFalse(diffCallbackWithSameItems.areItemsTheSame(0, 1))
        assertTrue(diffCallbackWithSameItems.areContentsTheSame(0, 0))
        assertFalse(diffCallbackWithSameItems.areContentsTheSame(0, 1))
    }

    data class TestObject(private val property: String)
}