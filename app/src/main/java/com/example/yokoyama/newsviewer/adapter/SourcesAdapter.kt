package com.example.yokoyama.newsviewer.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.yokoyama.newsviewer.R
import com.example.yokoyama.newsviewer.data.SourceIconMap
import com.example.yokoyama.newsviewer.newsapi.NewsSourceResult
import kotlinx.android.synthetic.main.check_box_list_item.view.*

class SourcesAdapter(private val context: Context,
                     private val showCheckBox : Boolean,
                     val sources: List<NewsSourceResult.NewsSource>,
                     val selectedSources: MutableList<NewsSourceResult.NewsSource>) : RecyclerView.Adapter<SourcesAdapter.ViewHolder>() {

    private var onBind : Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.check_box_list_item, parent, false))

    override fun getItemCount(): Int = sources.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewItem.text = sources[position].name
        holder.checkBoxItem.visibility = when (showCheckBox) {
            true -> View.VISIBLE
            false -> View.GONE
        }

        onBind = true
        holder.checkBoxItem.isChecked = selectedSources.contains(sources[position])
        onBind = false

        val imageResource = SourceIconMap.instance[sources[position].id] ?: R.drawable.sources_no_image
        holder.imageViewItem.setImageResource(imageResource)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewItem : TextView = itemView.textViewItemLabel
        val imageViewItem : ImageView = itemView.imageViewItem
        val checkBoxItem : CheckBox = itemView.checkBoxItem

        init {
            checkBoxItem.setOnCheckedChangeListener { _, checked ->
                if (!onBind) {
                    when (checked) {
                        true -> if (selectedSources.size < 20) selectedSources += sources[adapterPosition]
                                else Toast.makeText(context, R.string.warning_too_many_sources, Toast.LENGTH_SHORT).show()
                        false -> selectedSources -= sources[adapterPosition]
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

}






