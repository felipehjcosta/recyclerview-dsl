package com.github.felipecosta.recyclerviewdsl.sample

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.github.felipecosta.recyclerviewdsl.R
import com.github.felipecosta.recyclerviewdsl.sample.empty.EmptyFragment
import com.github.felipecosta.recyclerviewdsl.sample.objects.ObjectsFragment
import com.github.felipecosta.recyclerviewdsl.sample.strings.StringsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        setupToolbar()
        drawerLayout = findViewById(R.id.drawer_layout)

        findViewById<NavigationView>(R.id.nav_view).apply {
            setNavigationItemSelectedListener { menuItem ->
                menuItem.isChecked = true
                drawerLayout.closeDrawers()
                swapFragments(menuItem.itemId)

                true
            }
        }
        swapFragments(R.id.nav_strings_sample)
    }

    private fun swapFragments(itemId: Int) {
        val fragmentClassName = when (itemId) {
            R.id.nav_strings_sample -> StringsFragment::class.java.name
            R.id.nav_objects_sample -> ObjectsFragment::class.java.name
            R.id.nav_empty_sample -> EmptyFragment::class.java.name
            else -> null
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, Fragment.instantiate(this, fragmentClassName))
                .commit()
    }

    private fun setupToolbar() {
        findViewById<Toolbar>(R.id.toolbar).apply {
            setSupportActionBar(this)
        }
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_24dp)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
