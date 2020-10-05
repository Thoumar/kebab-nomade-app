package com.thoumar.kebabnomade.entities

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Restaurant(
    var cover: String,
    var name: String,
    var description: String,
    var address: String,
    var latitude: Double,
    var longitude: Double,
    var sauces: ArrayList<String>,
    var range: Double?
) : ClusterItem, Parcelable {
    constructor() : this(
        "https://i2.wp.com/lourdesactu.fr/wp-content/uploads/2020/04/placeholder.png?fit=1200%2C800",
        "Test",
        "test",
        "test",
        0.0,
        0.0,
        ArrayList(emptyList<String>()),
        0.0
    ) {
        val sauces = ArrayList<String>()
        sauces.add("blanche")
        sauces.add("samourai")
        sauces.add("algerienne")
        sauces.add("blanche-harissa")
        sauces.add("ketchup")
        sauces.add("mayonnaise")
        sauces.add("ketchup-mayo")
        sauces.add("andalouse")
        sauces.add("poivre")
        sauces.add("bearnaise")
        this.sauces = sauces
    }

    override fun getPosition(): LatLng {
        return LatLng(latitude, longitude)
    }

    override fun getTitle(): String? {
        return this.name
    }

    override fun getSnippet(): String? {
        return this.name
    }
} 