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
        mockkConstructor(SimpleRecyclerViewAdapter::class)

        val items = listOf("Spider-Man", "Thor", "Iron Main")

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
    fun ensureAdapterIsUpdatedWhenDeclareItOnDSL() {
        val extraItems = listOf("Spider-Man", "Thor", "Iron Main")

        val mockAdapter = mockk<SimpleRecyclerViewAdapter>(relaxed = true)
        val slot = slot<AdapterConfigurationMapping>()
        every { mockAdapter.update(newLayoutBinds = capture(slot)) } just runs
        recyclerView.adapter = mockAdapter

        onRecyclerView(recyclerView) {
            bind(android.R.layout.simple_list_item_1) {
                addExtraItems(extraItems)
            }
        }

        val expectedConfigurationExtraData = AdapterConfiguration().apply {
            adapterConfigurationExtraData = AdapterConfigurationExtraData(extraItems)
        }

        val capturedConfiguration = slot.captured

        assertThat(capturedConfiguration[android.R.layout.simple_list_item_1]).isEqualTo(expectedConfigurationExtraData)
    }
}