package com.thoumar.kebabnomade.adapters.sauces

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thoumar.kebabnomade.R
import kotlinx.android.synthetic.main.sauce_item.view.*


class SaucesAdapter(private val list: List<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return SauceViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val sauce: String = list[position]
        holder as SauceViewHolder
        holder.bind(sauce)
    }

    override fun getItemCount(): Int = list.size
}

class SauceViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.sauce_item, parent, false)) {

    fun bind(sauce: String) {
        itemView.sauceName.text = sauce


        val sauceIcon: Drawable? = when (sauce) {
            "blanche" -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_article)
            "samourai" -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_article)
            "algerienne" -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_article)
            "blanche-harissa" -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_article)
            "ketchup" -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_ketchup)
            "mayonnaise" -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_mayonnaise)
            "ketchup-mayo" -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_article)
            "andalouse" -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_article)
            "poivre" -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_article)
            "bearnaise" -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_article)
            else -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_article)
        }

        Glide
            .with(itemView)
            .load(sauceIcon)
            .into(itemView.sauceIcon)
    }
}