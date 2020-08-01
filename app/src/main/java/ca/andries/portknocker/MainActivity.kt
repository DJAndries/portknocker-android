package ca.andries.portknocker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import java.net.InetSocketAddress
import java.net.Socket

class MainActivity : AppCompatActivity() {

    var menu : Menu? = null

    val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.elevation = 0f

        ProfileManager.context = applicationContext

        viewPager.adapter = MainCollectionAdapter()
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                1 -> getString(R.string.profiles)
                else -> getString(R.string.quick_knock)
            }
        }.attach()

        viewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.main, menu)
        pageChangeCallback.onPageSelected(0)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this, AddProfileActivity::class.java)
        startActivityForResult(intent, 0)
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
            val addBtnEnabled = position == 1
            addBtn?.isEnabled = addBtnEnabled
            addBtn?.isVisible = addBtnEnabled
        }
    }

    inner class MainCollectionAdapter : FragmentStateAdapter(this) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                1 -> profileFragment
                else -> QuickKnockFragment()
            }
        }
    }
}