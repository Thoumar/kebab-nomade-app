package com.thoumar.kebabnomade.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Restaurant(
    var cover: String,
    var name: String,
    var description: String,
    var address: String,
    var latitude: Double,
    var longitude: Double,
) : Parcelable {
    constructor() : this("https://i2.wp.com/lourdesactu.fr/wp-content/uploads/2020/04/placeholder.png?fit=1200%2C800", "Test", "test", "test", 0.0, 0.0)
}