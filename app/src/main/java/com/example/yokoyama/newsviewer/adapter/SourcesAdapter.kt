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
import com.example.yokoyama.newsviewer.SourceIconMap
import com.example.yokoyama.newsviewer.newsapi.NewsSourceResult
import kotlinx.android.synthetic.main.check_box_list_item.view.*

class SourcesAdapter(private val context: Context,
                     val sources: List<NewsSourceResult.NewsSource>,
                     val checkedPositions: MutableList<Int>) : RecyclerView.Adapter<SourcesAdapter.ViewHolder>() {

    private var onBind : Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.check_box_list_item, parent, false))

    override fun getItemCount(): Int = sources.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewItem.text = sources[position].name

        onBind = true
        holder.checkBoxItem.isChecked = checkedPositions.contains(position)
        onBind = false

        val imageResource = SourceIconMap.instance[sources[position].id] ?: R.drawable.sources_no_image
        holder.imageViewItem.setImageResource(imageResource)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBoxItem : CheckBox = itemView.checkBoxItem
        val textViewItem : TextView = itemView.textViewItemLabel
        val imageViewItem : ImageView = itemView.imageViewItem

        init {
            checkBoxItem.setOnCheckedChangeListener { _, checked ->
                if (!onBind) {
                    when (checked) {
                        true -> if (checkedPositions.size < 20) checkedPositions += adapterPosition
                                else Toast.makeText(context, R.string.warning_too_many_sources, Toast.LENGTH_SHORT).show()
                        false -> checkedPositions -= adapterPosition
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

}






