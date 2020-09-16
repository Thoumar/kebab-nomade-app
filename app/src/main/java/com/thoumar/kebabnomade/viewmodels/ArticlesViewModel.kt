package com.thoumar.kebabnomade.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.thoumar.kebabnomade.entities.Article


class ArticlesViewModel : ViewModel() {

    val db = Firebase.firestore
    val TAG = "ArticlesViewModel"

    private val articles: MutableLiveData<List<Article>> by lazy {
        MutableLiveData<List<Article>>().also {
            loadArticles()
        }
    }

    fun getArticles(): LiveData<List<Article>> {
        return articles
    }

    private fun loadArticles() {
        db.collection("articles")
            .get()
            .addOnSuccessListener { documents ->
                articles.value = documents.toObjects(Article::class.java)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}