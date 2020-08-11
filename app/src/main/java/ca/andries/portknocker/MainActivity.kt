package ca.andries.portknocker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var menu : Menu? = null

    val quickKnockFragment = QuickKnockFragment { historyFragment.updateData(this) }

    val profileFragment = ProfileFragment { historyFragment.updateData(this) }

    val historyFragment = HistoryFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.elevation = 0f

        viewPager.adapter = MainCollectionAdapter()
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                1 -> getString(R.string.profiles)
                2 -> getString(R.string.history)
                else -> getString(R.string.quick_knock)
            }
        }.attach()

        viewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.main, menu)
        pageChangeCallback.onPageSelected(viewPager.currentItem)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addBtn -> {
                val intent = Intent(this, AddProfileActivity::class.java)
                startActivityForResult(intent, 0)
            }
            R.id.deleteBtn -> {
                historyFragment.deleteHistory()
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            profileFragment.updateData()
        }
    }

    val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position : Int) {
            val addBtn = menu?.findItem(R.id.addBtn)
            val deleteBtn = menu?.findItem(R.id.deleteBtn)
            val addBtnEnabled = position == 1
            val delBtnEnabled = position == 2
            addBtn?.isEnabled = addBtnEnabled
            addBtn?.isVisible = addBtnEnabled
            deleteBtn?.isEnabled = delBtnEnabled
            deleteBtn?.isVisible = delBtnEnabled
        }
    }

    inner class MainCollectionAdapter : FragmentStateAdapter(this) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                1 -> profileFragment
                2 -> historyFragment
                else -> quickKnockFragment
            }
        }
    }
}