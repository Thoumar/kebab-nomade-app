package com.thoumar.kebabnomade.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.thoumar.kebabnomade.entities.Restaurant


class RestaurantsViewModel : ViewModel() {

    val db = Firebase.firestore
    val TAG = "RestaurantsViewModel"

    private val restaurants: MutableLiveData<List<Restaurant>> by lazy {
        MutableLiveData<List<Restaurant>>().also {
            loadRestaurants()
        }
    }

    var filterTextAll = MutableLiveData<String>()

    fun getRestaurants(): LiveData<List<Restaurant>> {
        return restaurants
    }

    private fun loadRestaurants() {
        db.collection("restaurants")
            .get()
            .addOnSuccessListener { documents ->
                val allRestaurants = documents.toObjects(Restaurant::class.java)
                restaurants.value = allRestaurants

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}