package com.thoumar.kebabnomade.activities

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.thoumar.kebabnomade.R
import com.thoumar.kebabnomade.entities.Restaurant
import com.thoumar.kebabnomade.viewmodels.RestaurantsViewModel
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {

    private lateinit var restaurants: List<Restaurant>
    private lateinit var restaurantsViewModel: RestaurantsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // View Model
        val restaurantsViewModel: RestaurantsViewModel by viewModels()

        search_bar.requestFocus()

        initToolbar()
        initComponent()
    }

    private fun initToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar.navigationIcon!!.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initComponent() {
        progress_bar.visibility = View.GONE
        et_search.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                searchAction()
                return@OnEditorActionListener true
            }
            false
        })
        fab.setOnClickListener { searchAction() }
    }

    private fun searchAction() {
        progress_bar.visibility = View.VISIBLE
        fab.alpha = 0f


        Handler().postDelayed({
            progress_bar.visibility = View.GONE
            fab.alpha = 1f
            Toast.makeText(applicationContext, "Search Submit", Toast.LENGTH_SHORT).show()
        }, 1000)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else {
            Toast.makeText(applicationContext, item.title, Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
