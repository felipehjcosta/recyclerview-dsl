package com.github.felipecosta.recyclerviewdsl.sample.empty

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.github.felipecosta.recyclerviewdsl.R
import com.github.felipehjcosta.recyclerviewdsl.onRecyclerView

class EmptyFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recyclerview_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        val listOfNulls = arrayOfNulls<Any?>(7).toList()

        onRecyclerView(recyclerView) {
            withLinearLayout {
                orientation = RecyclerView.VERTICAL
                reverseLayout = false
            }

            bind(R.layout.empty_list_item) {
                withItems(listOfNulls) {
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
                orientation = RecyclerView.VERTICAL
                reverseLayout = false
            }
        }
    }

    private fun changeToRecyclerViewToGridLayout() {
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.recycler_view)
        onRecyclerView(recyclerView) {
            withGridLayout {
                orientation = RecyclerView.VERTICAL
                reverseLayout = false
                spanCount = 2
            }
        }
    }
}