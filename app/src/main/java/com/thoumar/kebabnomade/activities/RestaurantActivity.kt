package com.thoumar.kebabnomade.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.thoumar.kebabnomade.R
import com.thoumar.kebabnomade.entities.Restaurant
import kotlinx.android.synthetic.main.activity_restaurant.*

class RestaurantActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        val restaurant: Restaurant = intent.getParcelableExtra("RESTAURANT")

        Glide.with(this).load(restaurant.cover).into(image_view_restaurant_cover)

        text_view_restaurant_name.text = restaurant.name

        if (!restaurant.description.isNullOrEmpty()) {
            text_view_restaurant_description.text = restaurant.description
        } else {
            text_view_restaurant_description.visibility = View.GONE
        }

        if (!restaurant.sauces.isNullOrEmpty()) {
            text_view_sauces.text = restaurant.sauces.toString()
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

    }
}