package com.github.felipecosta.recyclerviewdsl.sample.strings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.github.felipecosta.recyclerviewdsl.R
import com.github.felipehjcosta.recyclerviewdsl.onRecyclerView

class StringsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.strings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

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
                bind(R.layout.strings_list_item) {
                    on<TextView>(R.id.title) {
                        it.view?.text = it.item
                    }

                    onClick { position, string ->
                        Toast.makeText(context,
                                "Position $position clicked for item: ${string}",
                                Toast.LENGTH_SHORT)
                                .show()
                    }
                }
            }
        }
    }
}
