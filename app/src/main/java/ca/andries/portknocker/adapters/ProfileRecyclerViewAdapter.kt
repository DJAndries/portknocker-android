package ca.andries.portknocker.adapters

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import ca.andries.portknocker.models.Profile
import ca.andries.portknocker.R

class ProfileRecyclerViewAdapter(
    private val values: List<Profile>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ProfileRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_profile, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.name.text = item.name
        holder.host.text = item.host
        val nextPorts = item.peekNextSequenceText()
        if (nextPorts.isNotEmpty()) {
            holder.host.text = "${holder.host.text} ($nextPorts)"
        }
        holder.index = position
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        var index: Int = 0
        val name: TextView = view.findViewById(R.id.name)
        val host: TextView = view.findViewById(R.id.host)

        init {
            view.setOnCreateContextMenuListener(this)
            view.setOnClickListener {
                onItemClick(index)
            }
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu.add(index,
                EDIT, 0, v.context.getString(
                    R.string.edit
                ))
            menu.add(index,
                DELETE, 0, v.context.getString(
                    R.string.delete
                ))
        }

        override fun toString(): String {
            return super.toString() + " '" + name.text + "'"
        }

    }

    companion object {
        val EDIT = View.generateViewId()
        val DELETE = View.generateViewId()
    }
}