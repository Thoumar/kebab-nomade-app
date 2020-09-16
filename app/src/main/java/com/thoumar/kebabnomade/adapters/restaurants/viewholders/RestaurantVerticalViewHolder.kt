package com.thoumar.kebabnomade.adapters.restaurants.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thoumar.kebabnomade.R
import com.thoumar.kebabnomade.entities.Restaurant
import kotlinx.android.synthetic.main.restaurant_horizontal_item.view.*
import kotlinx.android.synthetic.main.restaurant_vertical_item.view.*
import kotlinx.android.synthetic.main.restaurant_vertical_item.view.restaurantCard
import kotlinx.android.synthetic.main.restaurant_vertical_item.view.restaurantCover
import kotlinx.android.synthetic.main.restaurant_vertical_item.view.restaurantName

class RestaurantVerticalViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.restaurant_vertical_item, parent, false)) {

    fun bind(restaurant: Restaurant, click: ((restaurant: Restaurant) -> Unit)) {
        itemView.restaurantName.text = restaurant.name

        Glide
            .with(itemView)
            .load(restaurant.cover)
            .into(itemView.restaurantCover)

        itemView.restaurantCard.setOnClickListener {
            click(restaurant)
        }
    }
}