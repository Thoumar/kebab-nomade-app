package com.thoumar.kebabnomade.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.thoumar.kebabnomade.entities.Restaurant

class RestaurantsRepository {

    val TAG = "RestaurantsRepository"

    // Access a Cloud Firestore instance from your Activity
    val db = Firebase.firestore

}