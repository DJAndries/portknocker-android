package ca.andries.portknocker.fragments

import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import ca.andries.portknocker.*
import ca.andries.portknocker.data.StoredDataManager
import ca.andries.portknocker.adapters.HistoryItemRecyclerViewAdapter
import ca.andries.portknocker.models.HistoryItem
import ca.andries.portknocker.util.AlertHelper
import kotlinx.android.synthetic.main.fragment_history_list.*
import kotlinx.android.synthetic.main.fragment_profile_list.view.emptyView
import kotlinx.android.synthetic.main.fragment_profile_list.view.list

/**
 * A fragment representing a list of Items.
 */
class HistoryFragment : Fragment() {

    private var columnCount = 1

    private val historyList : ArrayList<HistoryItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history_list, container, false)

        updateData(requireContext())
        updateVisibility(view)
        // Set the adapter
        val recyclerView = view.list
        with(recyclerView) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter =
                HistoryItemRecyclerViewAdapter(
                    historyList
                )
            addItemDecoration(DividerItemDecoration(context, ClipDrawable.HORIZONTAL))
        }
        return view
    }

    fun updateData(context: Context) {
        historyList.clear()
        historyList.addAll(
            StoredDataManager.listHistory(
                context
            )
        )
        if (this.view != null) {
            updateVisibility()
            list.adapter?.notifyDataSetChanged()
        }
    }

    fun deleteHistory() {
        AlertHelper.showConfirmDialog(
            requireContext(),
            R.string.clear_history_prompt
        ) {
            StoredDataManager.clearHistory(
                requireContext()
            )
            updateData(requireContext())
        }
    }

    private fun updateVisibility(view : View = this.requireView()) {
        if (historyList.isEmpty()) {
            view.list.visibility = View.GONE
            view.emptyView.visibility = View.VISIBLE
        } else {
            view.list.visibility = View.VISIBLE
            view.emptyView.visibility = View.GONE
            view.invalidate()
        }
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"
    }
}