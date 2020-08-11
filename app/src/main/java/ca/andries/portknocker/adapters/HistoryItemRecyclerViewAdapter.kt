package ca.andries.portknocker.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.andries.portknocker.models.HistoryItem
import ca.andries.portknocker.R
import java.text.DateFormat

class HistoryItemRecyclerViewAdapter(
    private val values: List<HistoryItem>
) : RecyclerView.Adapter<HistoryItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.hostTxt.text = item.host
        holder.portTxt.text = item.ports
        holder.dateTxt.text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(item.date)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hostTxt: TextView = view.findViewById(R.id.hostTxt)
        val portTxt: TextView = view.findViewById(R.id.portTxt)
        val dateTxt: TextView = view.findViewById(R.id.dateTxt)

        override fun toString(): String {
            return super.toString() + " '" + hostTxt.text + ":" + portTxt.text + "'"
        }
    }
}