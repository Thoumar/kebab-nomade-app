package com.thoumar.kebabnomade.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.thoumar.kebabnomade.R
import com.thoumar.kebabnomade.adapters.sauces.SaucesAdapter
import com.thoumar.kebabnomade.entities.Restaurant
import kotlinx.android.synthetic.main.activity_restaurant.*

class RestaurantActivity : AppCompatActivity() {

    private lateinit var restaurant: Restaurant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        restaurant = intent.getParcelableExtra("RESTAURANT")

        Glide.with(this).load(restaurant.cover).into(image_view_restaurant_cover)

        if (!restaurant.description.isNullOrEmpty()) {
            text_view_restaurant_description.text = restaurant.description
        } else {
            text_view_restaurant_description.visibility = View.GONE
        }

        if (!restaurant.sauces.isNullOrEmpty()) {

            sauces_recycler_view.apply {
                layoutManager =
                    LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = SaucesAdapter(restaurant.sauces)
            }
        }

        text_view_restaurant_address.text = restaurant.address

        button_restaurant_go_to_location.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=${restaurant.latitude},${restaurant.longitude} (" + restaurant.name + ")")
            )
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }
        button_restaurant_share.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "T'as essayé ce restaurant: " + restaurant.name)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        button_restaurant_favorites.setOnClickListener {

        }

        initToolbar()
    }


    private fun initToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar.navigationIcon!!.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = restaurant.name
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else {
            Toast.makeText(applicationContext, item.title, Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}