package com.github.felipehjcosta.recyclerviewdsl

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SimpleRecyclerViewAdapterTest {

    private val activityController = Robolectric.buildActivity(Activity::class.java)

    private val mockAdapterItemBinder = mockk<AdapterItemBinder>(relaxed = true)

    private val adapterConfigurationMapping = provideConfiguration()

    private val adapter = SimpleRecyclerViewAdapter(adapterConfigurationMapping)

    @Test
    fun ensureAdapterItemViewTypeIsCorrect() {
        for (i in 0 until 3) {
            assertThat(adapter.getItemViewType(i)).isZero()
        }
    }

    @Test
    fun ensureAdapterCreateCorrectHolder() {
        val holder = adapter.onCreateViewHolder(FrameLayout(activityController.get()), 0)

        assertThat(holder).isNotNull()
    }

    @Test
    fun ensureAdapterBindHolder() {
        val holder = adapter.onCreateViewHolder(FrameLayout(activityController.get()), 0)

        val spiedHolder = spyk(holder)

        every { spiedHolder.itemViewType } returns 0

        adapter.bindViewHolder(spiedHolder, 0)

        val bindView = holder.itemView.findViewById<View>(android.R.id.text1)
        val bindItem = "Spider-Man"

        verify { mockAdapterItemBinder.bind(bindItem, bindView) }
    }

    @Test
    fun ensureAdapterUpdatesMappingWhenReceivesExtraDataMapping() {
        val spied = spyk(adapter)

        val extraAdapterConfigurationMapping = provideExtraConfiguration()
        val extraKey = android.R.layout.simple_list_item_1
        val extraAdapterConfigurationExtraData = extraAdapterConfigurationMapping[extraKey]!!.adapterConfigurationExtraData!!
        spied.addExtra(extraKey, extraAdapterConfigurationExtraData)


        val expectedItems = listOf("Spider-Man", "Thor", "Iron Main", "Hulk")
        val currentAdapterConfiguration = adapter.adapterConfigurationMapping.get(android.R.layout.simple_list_item_1)
        assertThat(currentAdapterConfiguration.adapterConfigurationData?.items).isEqualTo(expectedItems)
        verify { spied.notifyItemRangeInserted(3, 1) }
    }


    @Test
    fun ensureAdapterReplaceDataWhenUpdatesData() {
        val spied = spyk(adapter)

        val newItems = listOf("Hulk", "Captain America", "Hawkeye")
        val key = android.R.layout.simple_list_item_1
        val adapterConfigurationData = provideConfiguration(newItems)[key].adapterConfigurationData

        spied.update(key, adapterConfigurationData!!)

        val currentAdapterConfiguration = adapter.adapterConfigurationMapping[android.R.layout.simple_list_item_1]
        assertThat(currentAdapterConfiguration.adapterConfigurationData).isEqualTo(adapterConfigurationData)
        verify { spied.notifyDataSetChanged() }
    }

    private fun provideConfiguration(items: List<String> = listOf("Spider-Man", "Thor", "Iron Main")): AdapterConfigurationMapping {
        return AdapterConfigurationMapping().apply {
            append(android.R.layout.simple_list_item_1, AdapterConfiguration().apply {
                adapterConfigurationData = AdapterConfigurationData(items.map { it as Any? }.toMutableList(), String::class).apply {
                    adapterItemBinderMapping.append(android.R.id.text1, mockAdapterItemBinder)
                }
            })
        }
    }

    private fun provideExtraConfiguration(items: List<String> = listOf("Hulk")): AdapterConfigurationMapping {
        return AdapterConfigurationMapping().apply {
            append(android.R.layout.simple_list_item_1, AdapterConfiguration().apply {
                adapterConfigurationExtraData = AdapterConfigurationExtraData(items.map { it as Any? }.toMutableList())
            })
        }
    }
}
