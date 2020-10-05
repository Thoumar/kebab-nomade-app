package com.thoumar.kebabnomade.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thoumar.kebabnomade.R
import com.thoumar.kebabnomade.activities.ArticleActivity
import com.thoumar.kebabnomade.activities.RestaurantActivity
import com.thoumar.kebabnomade.activities.SearchActivity
import com.thoumar.kebabnomade.adapters.articles.ArticlesAdapter
import com.thoumar.kebabnomade.adapters.restaurants.RestaurantsAdapter
import com.thoumar.kebabnomade.entities.Article
import com.thoumar.kebabnomade.entities.Restaurant
import com.thoumar.kebabnomade.viewmodels.ArticlesViewModel
import com.thoumar.kebabnomade.viewmodels.RestaurantsViewModel
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private lateinit var articles: List<Article>
    private lateinit var restaurants: List<Restaurant>
    private lateinit var articlesViewModel: ArticlesViewModel
    private lateinit var restaurantsViewModel: RestaurantsViewModel

    class Message(var title: String, var qualif: String, var ponctuation: String)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)

        // View Model
        val articlesViewModel: ArticlesViewModel by viewModels()
        val restaurantsViewModel: RestaurantsViewModel by viewModels()

        articlesViewModel.getArticles().observe(requireActivity(), {
            articles = it
            setArticles(v.articlesRcView)
        })

        restaurantsViewModel.getRestaurants().observe(requireActivity(), {
            restaurants = it
            setRestaurants(v.restaurantsRcView)
        })

        setWelcomeMessage(v)

        v.edit_text_searchbar.setOnClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
        }
        return v
    }

    private fun setWelcomeMessage(v: View) {
        val titles = arrayListOf("Comment ça va ", "Bonjour ")
        val qualifs = arrayListOf(" beau gosse ", " chef ")
        val ponctuations = arrayListOf("? ", "! ")

        val randomTitle = titles.shuffled().take(1)[0]
        val randomQualifs = qualifs.shuffled().take(1)[0]
        val randomPonctuation = ponctuations.shuffled().take(1)[0]

        v.txt_welcome_main.text = randomTitle
        v.txt_welcome_qualif.text = randomQualifs
        v.txt_welcome_ponctuation.text = randomPonctuation
    }

    private fun setArticles(rc: RecyclerView) = rc.apply {
        layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        adapter = ArticlesAdapter("horizontal", articles) { article -> onArticleClick(article) }
    }

    private fun onArticleClick(article: Article) {
        Toast.makeText(requireContext(), article.title, Toast.LENGTH_LONG).show()
        val intent = Intent(activity, ArticleActivity::class.java)
        intent.putExtra("ARTICLE", article)
        startActivity(intent)
    }

    private fun setRestaurants(rc: RecyclerView) = rc.apply {
        layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        adapter = RestaurantsAdapter(
            "vertical",
            restaurants
        ) { restaurant -> onRestaurantClick(restaurant) }
    }

    private fun onRestaurantClick(restaurant: Restaurant) {
        Toast.makeText(requireContext(), restaurant.name, Toast.LENGTH_LONG).show()
        val intent = Intent(activity, RestaurantActivity::class.java)
        intent.putExtra("RESTAURANT", restaurant)
        startActivity(intent)
    }
}