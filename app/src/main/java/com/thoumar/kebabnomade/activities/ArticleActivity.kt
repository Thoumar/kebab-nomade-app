package com.thoumar.kebabnomade.activities

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.thoumar.kebabnomade.R
import com.thoumar.kebabnomade.entities.Article
import kotlinx.android.synthetic.main.activity_article.*
import java.text.DateFormatSymbols


class ArticleActivity : AppCompatActivity() {

    private var fab: FloatingActionButton? = null
    var hide = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        val article: Article = intent.getParcelableExtra("ARTICLE")

        Glide
            .with(this)
            .load(article.cover)
            .into(image_view_article_cover)

        text_view_article_title.text = article.title
        text_view_article_snippet.text = article.snippet

        web_view_article_body.loadData(article.body, "text/html", "UTF-8")
        web_view_article_body.setBackgroundColor(Color.parseColor("#17181A"))

        val date = article.publicationDate

        text_view_publication_date.text = getMonthForInt(date.month) + " " + date.day
        text_view_read_time.text = article.timeRead.toString() + " min read"
        text_view_likes_count.text = article.likes.toString() + " Likes"

        Glide
            .with(this)
            .load(article.authorPicture)
            .circleCrop()
            .into(image_view_author_profile_picture)

        text_view_author_name.text = "Thomas Oumar"
        btn_article_like.setOnClickListener {
            Toast.makeText(this, "Like added", Toast.LENGTH_LONG).show()
        }
        initLikeButtonAnimation()
    }

    private fun getMonthForInt(num: Int): String? {
        var month = "wrong"
        val dfs = DateFormatSymbols()
        val months: Array<String> = dfs.months
        if (num in 0..11) {
            month = months[num]
        }
        return month.capitalize()
    }

    private fun initLikeButtonAnimation() {
        nested_scroll_view.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                hide = if (scrollY >= oldScrollY) {
                    if (hide) return@OnScrollChangeListener
                    val moveY = 2 * fab!!.height
                    fab!!.animate()
                        .translationY(moveY.toFloat())
                        .setDuration(300)
                        .start()
                    true
                } else {
                    if (!hide) return@OnScrollChangeListener
                    fab!!.animate()
                        .translationY(0f)
                        .setDuration(300)
                        .start()
                    false
                }
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_article_share_save, menu)
        return true
    }
}