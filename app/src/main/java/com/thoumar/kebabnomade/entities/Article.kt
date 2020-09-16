package com.thoumar.kebabnomade.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Article(
    var cover: String,
    var title: String,
    var snippet: String,
    var body: String
) : Parcelable {
    constructor() : this("https://i2.wp.com/lourdesactu.fr/wp-content/uploads/2020/04/placeholder.png?fit=1200%2C800", "Nouvel article", "test", "test")
}