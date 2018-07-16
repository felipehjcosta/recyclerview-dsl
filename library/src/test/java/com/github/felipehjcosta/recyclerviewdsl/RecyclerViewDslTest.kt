package com.github.felipehjcosta.recyclerviewdsl

import android.app.Activity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class RecyclerViewDslTest {

    private lateinit var recyclerView: RecyclerView

    @Before
    fun setUp() {
        val activityController = Robolectric.buildActivity(Activity::class.java)
        recyclerView = RecyclerView(activityController.get())
    }

    @Test
    fun ensureGridLayoutManagerIsUsedWhenDeclareItOnDSL() {
        onRecyclerView(recyclerView) {
            withGridLayout()
        }

        assertThat(recyclerView.layoutManager).isExactlyInstanceOf(GridLayoutManager::class.java)
    }

    @Test
    fun ensureLinearLayoutManagerIsUsedWhenDeclareItOnDSL() {
        onRecyclerView(recyclerView) {
            withLinearLayout()
        }

        assertThat(recyclerView.layoutManager).isExactlyInstanceOf(LinearLayoutManager::class.java)
    }

    @Test
    fun ensureCustomLayoutManagerIsUsedWhenDeclareItOnDSL() {
        val mockLayoutManager = mockk<RecyclerView.LayoutManager>(relaxed = true)
        onRecyclerView(recyclerView) {
            withLayout(mockLayoutManager)
        }

        assertThat(recyclerView.layoutManager).isEqualTo(mockLayoutManager)
    }

    @Test
    fun ensureDSLNotModifyRecyclerViewLayoutManagerWhenDeclareItOnDSL() {
        val mockLayoutManager = mockk<RecyclerView.LayoutManager>(relaxed = true)
        recyclerView.layoutManager = mockLayoutManager
        onRecyclerView(recyclerView) {

        }

        assertThat(recyclerView.layoutManager).isEqualTo(mockLayoutManager)
    }

    @Test
    fun ensureAdapterIsSetWhenDeclareItOnDSL() {
        val items = listOf("Spider-Man", "Thor", "Iron Man")

        onRecyclerView(recyclerView) {
            bind(android.R.layout.simple_list_item_1) {
                withItems(items) {
                    on<TextView>(android.R.id.text1) {
                        it.view?.text = it.item
                    }
                }
            }
        }

        val expectedConfiguration = AdapterConfiguration().apply {
            adapterConfigurationData = AdapterConfigurationData(items.map { it as Any? }.toMutableList(), String::class)
        }

        val configuration = (recyclerView.adapter as SimpleRecyclerViewAdapter).adapterConfigurationMapping
        assertThat(configuration[android.R.layout.simple_list_item_1]).isEqualTo(expectedConfiguration)
    }

    @Test
    fun ensureAdapterIsReceivingNewDataWhenDeclareItOnDSL() {
        val mockAdapter = mockk<SimpleRecyclerViewAdapter>(relaxed = true)
        val keySlot = slot<Int>()
        val extraDataSlot = slot<AdapterConfigurationExtraData<out Any>>()
        every { mockAdapter.addExtra(key = capture(keySlot), adapterConfigurationExtraData = capture(extraDataSlot)) } just runs

        val items = listOf("Hulk")
        val configuration = AdapterConfiguration().apply {
            adapterConfigurationData = AdapterConfigurationData(items.map { it as Any? }.toMutableList(), String::class)
        }
        val adapterConfigurationMapping = AdapterConfigurationMapping().apply {
            append(android.R.layout.simple_list_item_1, configuration)
        }

        every { mockAdapter.adapterConfigurationMapping } returns adapterConfigurationMapping

        recyclerView.adapter = mockAdapter

        val extraItems = listOf("Spider-Man", "Thor", "Iron Man")
        onRecyclerView(recyclerView) {
            bind(android.R.layout.simple_list_item_1) {
                addExtraItems(extraItems)
            }
        }

        val expectedConfigurationExtraData = AdapterConfiguration().apply {
            adapterConfigurationExtraData = AdapterConfigurationExtraData(extraItems)
        }

        val capturedKeySlot = keySlot.captured

        assertThat(android.R.layout.simple_list_item_1).isEqualTo(capturedKeySlot)

        val capturedExtraDataSlot = extraDataSlot.captured
        assertThat(expectedConfigurationExtraData.adapterConfigurationExtraData).isEqualTo(capturedExtraDataSlot)
    }

    @Test
    fun ensureAdapterIsUpdatedWhenDeclareItOnDSL() {
        val items = listOf("Hulk")
        onRecyclerView(recyclerView) {
            bind(android.R.layout.simple_list_item_1) {
                withItems(items) {
                    on<TextView>(android.R.id.text1) {
                        it.view?.text = it.item
                    }
                }
            }
        }

        val spiedAdapter = spyk(recyclerView.adapter as SimpleRecyclerViewAdapter)
        recyclerView.adapter = spiedAdapter

        val newItems = listOf("Spider-Man", "Thor", "Iron Man")
        onRecyclerView(recyclerView) {
            bind(android.R.layout.simple_list_item_1) {
                withItems(newItems) {
                    on<TextView>(android.R.id.text1) {
                        it.view?.text = it.item
                    }
                }
            }
        }

        val expectedKey = android.R.layout.simple_list_item_1
        val expectedAdapterConfigurationData = AdapterConfigurationData(newItems.map { it as Any? }.toMutableList(), String::class)
        verify { spiedAdapter.update(expectedKey, expectedAdapterConfigurationData) }
    }

    @Test
    fun ensureNewAdapterIsCreatedWhenChangeLayout() {
        val items = listOf("Spider-Man", "Thor", "Iron Man")

        onRecyclerView(recyclerView) {
            withLinearLayout()
            bind(android.R.layout.simple_list_item_1) {
                withItems(items) {
                    on<TextView>(android.R.id.text1) {
                        it.view?.text = it.item
                    }
                }
            }
        }

        val oldAdapter = recyclerView.adapter

        onRecyclerView(recyclerView) {
            bind(android.R.layout.simple_list_item_2) {
                withItems(items) {
                    on<TextView>(android.R.id.text1) {
                        it.view?.text = it.item
                    }
                }
            }
        }

        val actualAdapter = recyclerView.adapter
        assertThat(actualAdapter).isNotEqualTo(oldAdapter)
    }
}