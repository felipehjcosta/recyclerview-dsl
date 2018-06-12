package com.github.felipecosta.recyclerviewdsl.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.felipecosta.recyclerviewdsl.R
import com.github.felipehjcosta.recyclerviewdsl.onRecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        val titles = listOf(
                "Spider-Man",
                "Thor",
                "Iron Main",
                "Black Panther",
                "Black Widow",
                "Captain America",
                "Captain Marvel",
                "Falcon",
                "Hank Pym",
                "Hawkeye",
                "Hulk")

        onRecyclerView(recyclerView) {
            withLinearLayout {
                orientation = LinearLayout.VERTICAL
                reverseLayout = false
            }

            withItems(titles) {
                bind(R.layout.recycler_view_item) {
                    on<String, TextView>(R.id.title) {
                        it.view?.text = it.item
                    }
                }
            }
        }
    }
}
