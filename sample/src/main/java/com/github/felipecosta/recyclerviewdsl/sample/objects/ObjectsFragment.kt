package com.github.felipecosta.recyclerviewdsl.sample.objects

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.github.felipecosta.recyclerviewdsl.R
import com.github.felipehjcosta.recyclerviewdsl.onRecyclerView

class ObjectsFragment : Fragment() {

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

        val items = DummyContent.ITEMS

        onRecyclerView(recyclerView) {
            withLinearLayout {
                orientation = RecyclerView.VERTICAL
                reverseLayout = false
            }

            bind(R.layout.objects_item_list) {
                withItems(items) {
                    on<TextView>(R.id.title) {
                        it.view?.text = it.item?.content
                    }

                    on<TextView>(R.id.description) {
                        it.view?.text = it.item?.details
                    }

                    onClick { position, item ->
                        Toast.makeText(
                            context,
                            "Position $position clicked for item: ${item?.content}",
                            Toast.LENGTH_SHORT
                        )
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
