package ca.andries.portknocker.fragments

import android.content.Intent
import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import ca.andries.portknocker.*
import ca.andries.portknocker.activities.AddProfileActivity
import ca.andries.portknocker.data.StoredDataManager
import ca.andries.portknocker.adapters.ProfileRecyclerViewAdapter
import ca.andries.portknocker.models.Profile
import ca.andries.portknocker.util.AlertHelper
import ca.andries.portknocker.util.KnockUtil
import kotlinx.android.synthetic.main.fragment_profile_list.*
import kotlinx.android.synthetic.main.fragment_profile_list.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class ProfileFragment(val onKnock: () -> Unit) : Fragment() {

    private var columnCount = 1

    private val profiles : ArrayList<Profile> = arrayListOf()

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
        val view = inflater.inflate(R.layout.fragment_profile_list, container, false)

        profiles.addAll(
            StoredDataManager.listProfiles(
                requireContext()
            )
        )
        updateVisibility(view)
        // Set the adapter
        val recyclerView = view.list
        if (recyclerView is RecyclerView) {
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter =
                    ProfileRecyclerViewAdapter(
                        profiles
                    ) { i ->
                        promptKnock(profiles[i])
                    }
                addItemDecoration(DividerItemDecoration(context, HORIZONTAL))
                registerForContextMenu(view)
            }
        }
        return view
    }

    private fun promptKnock(profile: Profile) {
        AlertHelper.showConfirmDialog(
            requireContext(),
            R.string.knock_prompt
        ) { startKnock(profile) }
    }

    private fun startKnock(profile: Profile) {
        val ports = try {
            if (profile.oneTimeEnabled) {
                profile.peekNextSequence(requireContext())
            } else {
                profile.ports
            }
        } catch (e : PortKnockerException) {
            Toast.makeText(context, e.msg, Toast.LENGTH_LONG).show()
            return
        }

        Toast.makeText(context, getString(R.string.knocking), Toast.LENGTH_SHORT).show()
        GlobalScope.launch {
            KnockUtil.knockPorts(
                requireContext(),
                profile.host,
                ports
            )

            var isSuccess = true
            if (profile.portCheckEnabled) {
                Thread.sleep(profile.portCheckWaitInterval.toLong())
                isSuccess = KnockUtil.knockPort(
                    profile.host,
                    profile.portToCheck
                )
            }

            if (isSuccess && profile.oneTimeEnabled) {
                profile.popNextSequence(requireContext())
                StoredDataManager.saveProfile(
                    requireContext(),
                    profile
                )
            }

            activity?.runOnUiThread {
                val msg = if (isSuccess) R.string.open_sesame else R.string.failed_knock
                Toast.makeText(context, getString(msg, ports.joinToString()), Toast.LENGTH_LONG).show()
                if (profile.oneTimeEnabled) updateData()
                onKnock()
            }
        }
    }

    fun updateData() {
        profiles.clear()
        profiles.addAll(
            StoredDataManager.listProfiles(
                requireContext()
            )
        )
        list.adapter?.notifyDataSetChanged()
        updateVisibility()
    }

    private fun updateVisibility(view : View = this.requireView()) {
        if (profiles.isEmpty()) {
            view.list.visibility = GONE
            view.emptyView.visibility = VISIBLE
        } else {
            view.list.visibility = VISIBLE
            view.emptyView.visibility = GONE
            view.invalidate()
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            ProfileRecyclerViewAdapter.EDIT -> {
                editProfile(item.groupId)
            }
            ProfileRecyclerViewAdapter.DELETE -> {
                deleteProfile(item.groupId)
            }
        }
        return true
    }

    private fun editProfile(index: Int) {
        val profile = profiles[index]
        val intent = Intent(activity, AddProfileActivity::class.java)
        intent.putExtra(AddProfileActivity.EXISTING_PROFILE, profile)
        startActivityForResult(intent, 0)
    }

    private fun deleteProfile(index: Int) {
        AlertHelper.showConfirmDialog(
            requireContext(),
            R.string.delete_prompt
        ) {
            StoredDataManager.deleteProfile(
                requireContext(),
                index
            )
            updateData()
        }
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
    }
}