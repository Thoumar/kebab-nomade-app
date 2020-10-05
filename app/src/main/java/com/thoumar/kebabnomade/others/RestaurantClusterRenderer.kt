package com.thoumar.kebabnomade.others

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.thoumar.kebabnomade.R
import com.thoumar.kebabnomade.entities.Restaurant


class RestaurantClusterRenderer(
    val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<Restaurant>
) : DefaultClusterRenderer<Restaurant>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(restaurant: Restaurant, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(restaurant, markerOptions)

        val decodedRessource =
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_marker_icon)
        val bitmap = Bitmap.createScaledBitmap(decodedRessource, 60, 60, false)
        val icon: BitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap)

        markerOptions.icon(icon)
    }
}