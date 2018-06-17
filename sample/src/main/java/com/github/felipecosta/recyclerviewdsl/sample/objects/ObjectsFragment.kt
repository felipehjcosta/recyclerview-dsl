package com.github.felipecosta.recyclerviewdsl.sample.objects

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

class ObjectsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.objects_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        val items = DummyContent.ITEMS

        onRecyclerView(recyclerView) {
            withLinearLayout {
                orientation = LinearLayout.VERTICAL
                reverseLayout = false
            }

            withItems(items) {
                bind(R.layout.objects_item_list) {
                    on<TextView>(R.id.title) {
                        it.view?.text = it.item?.content
                    }

                    on<TextView>(R.id.description) {
                        it.view?.text = it.item?.details
                    }

                    onClick { position, item ->
                        Toast.makeText(context,
                                "Position $position clicked for item: ${item.content}",
                                Toast.LENGTH_SHORT)
                                .show()
                    }
                }
            }
        }
    }
}
