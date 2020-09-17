package com.thoumar.kebabnomade.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.thoumar.kebabnomade.R
import com.thoumar.kebabnomade.entities.Article
import kotlinx.android.synthetic.main.activity_article.*

class ArticleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        val article: Article = intent.getParcelableExtra("ARTICLE")

        Glide.with(this).load(article.cover).into(image_view_article_cover)

        text_view_article_title.text = article.title
        text_view_article_snippet.text = article.snippet
        text_view_article_body.text = article.body


    }
}