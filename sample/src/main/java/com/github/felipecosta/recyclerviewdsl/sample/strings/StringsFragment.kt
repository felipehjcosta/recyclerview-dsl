package com.github.felipecosta.recyclerviewdsl.sample.strings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.github.felipecosta.recyclerviewdsl.R
import com.github.felipehjcosta.recyclerviewdsl.onRecyclerView


class StringsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recyclerview_fragment, container, false)
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.recyclerview_options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_linear_layout -> {
                changeToRecyclerViewToLinearLayout()
                true
            }
            R.id.action_grid_layout -> {
                changeToRecyclerViewToGridLayout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun changeToRecyclerViewToLinearLayout() {
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.recycler_view)
        onRecyclerView(recyclerView) {
            withLinearLayout {
                orientation = LinearLayout.VERTICAL
                reverseLayout = false
            }
        }
    }

    private fun changeToRecyclerViewToGridLayout() {
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.recycler_view)
        onRecyclerView(recyclerView) {
            withGridLayoutManager {
                orientation = LinearLayout.VERTICAL
                reverseLayout = false
                spanCount = 2
            }
        }
    }
}
