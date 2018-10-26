package com.example.yokoyama.newsviewer

import android.content.Context
import android.support.v4.widget.CircularProgressDrawable
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.yokoyama.newsviewer.newsapi.NewsResult
import kotlinx.android.synthetic.main.news_entry.view.*

class NewsEntryAdapter(private val context: Context,
                       private val articleListener: ArticleListener,
                       private val newsResult: NewsResult) : RecyclerView.Adapter<NewsEntryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.news_entry, parent, false))

    override fun getItemCount(): Int = newsResult.articles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewArticleTitle.text = newsResult.articles[position].title
        holder.textViewArticleDescription.text = newsResult.articles[position].description
        holder.textViewDatePublished.text = newsResult.articles[position].publishedAt

        val bufferingWheel = CircularProgressDrawable(context)
        bufferingWheel.strokeWidth = 5f
        bufferingWheel.centerRadius = 30f
        bufferingWheel.start()

        val requestOptions = RequestOptions()
            .fallback(R.drawable.no_thumbnail)
            .error(R.drawable.error_thumbnail)
            .placeholder(bufferingWheel)
            .override(600, 300)

        Glide.with(context)
            .load(newsResult.articles[position].urlToImage)
            .apply(requestOptions)
            .into(holder.imageViewArticleImage)
    }

    interface ArticleListener {
        fun articleSelected(newsEntry : NewsResult.NewsEntry)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val cardViewArticle : CardView = itemView.cardViewArticle
        val textViewArticleTitle : TextView = itemView.textViewArticleTitle
        val textViewArticleDescription: TextView = itemView.textViewArticleDescription
        val textViewDatePublished : TextView = itemView.textViewDatePublished
        val imageViewArticleImage : ImageView = itemView.imageViewArticleImage

        init {
            cardViewArticle.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            when {
                view?.id == R.id.cardViewArticle -> articleListener.articleSelected(newsResult.articles[adapterPosition])
            }
        }
    }

}