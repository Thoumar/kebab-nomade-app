package com.thoumar.kebabnomade.adapters.restaurants


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thoumar.kebabnomade.adapters.restaurants.viewholders.RestaurantHorizontalViewHolder
import com.thoumar.kebabnomade.adapters.restaurants.viewholders.RestaurantMapViewHolder
import com.thoumar.kebabnomade.adapters.restaurants.viewholders.RestaurantVerticalViewHolder
import com.thoumar.kebabnomade.entities.Restaurant

class RestaurantsAdapter(private val type: String, private val list: List<Restaurant>, private val click: ((restaurant: Restaurant) -> Unit)):  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when(type) {
            "horizontal" ->  RestaurantHorizontalViewHolder(inflater, parent)
            "vertical" -> RestaurantVerticalViewHolder(inflater, parent)
            "map" -> RestaurantMapViewHolder(inflater, parent)
            else ->  RestaurantVerticalViewHolder(inflater, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val restaurant: Restaurant = list[position]

        when(type) {
            "horizontal" -> {
                holder as RestaurantHorizontalViewHolder
                holder.bind(restaurant, click)
            }
            "vertical" -> {
                holder as RestaurantVerticalViewHolder
                holder.bind(restaurant, click)
            }
            "map" -> {
                holder as RestaurantMapViewHolder
                holder.bind(restaurant, click)
            }
            else ->   {
                holder as RestaurantHorizontalViewHolder
                holder.bind(restaurant, click)
            }
        }
    }
    override fun getItemCount(): Int = list.size
}