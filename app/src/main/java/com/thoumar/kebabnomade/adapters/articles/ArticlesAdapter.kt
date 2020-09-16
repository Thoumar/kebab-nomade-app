package com.thoumar.kebabnomade.adapters.articles


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thoumar.kebabnomade.adapters.articles.viewholders.ArticleHorizontalViewHolder
import com.thoumar.kebabnomade.adapters.articles.viewholders.ArticleVerticalViewHolder
import com.thoumar.kebabnomade.entities.Article

class ArticlesAdapter(private val type: String, private val list: List<Article>, private val click: ((article: Article) -> Unit)):  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when(type) {
            "horizontal" ->  ArticleHorizontalViewHolder(inflater, parent)
            "vertical" -> ArticleVerticalViewHolder(inflater, parent)
            else ->  ArticleVerticalViewHolder(inflater, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val article: Article = list[position]

        when(type) {
            "horizontal" -> {
                holder as ArticleHorizontalViewHolder
                holder.bind(article, click)
            }
            "vertical" -> {
                holder as ArticleVerticalViewHolder
                holder.bind(article, click)
            }
            else ->   {
                holder as ArticleHorizontalViewHolder
                holder.bind(article, click)
            }
        }
    }
    override fun getItemCount(): Int = list.size
}