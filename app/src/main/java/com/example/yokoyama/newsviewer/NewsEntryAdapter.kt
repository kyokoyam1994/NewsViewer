package com.example.yokoyama.newsviewer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.news_entry.view.*

class NewsEntryAdapter(private val newsEntries: List<NewsResult.NewsEntry>) : RecyclerView.Adapter<NewsEntryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.news_entry, parent, false))
    }

    override fun getItemCount(): Int {
        return newsEntries.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewArticleTitle.text = newsEntries[position].title
        holder.textViewArticleDescription.text = newsEntries[position].description
        holder.textViewDatePublished.text = newsEntries[position].publishedAt
        //holder.imageViewArticleImage
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewArticleTitle : TextView = itemView.textViewArticleTitle
        val textViewArticleDescription: TextView = itemView.textViewArticleDescription
        val textViewDatePublished : TextView = itemView.textViewDatePublished
        val imageViewArticleImage : ImageView = itemView.imageViewArticleImage
    }

}