package com.example.yokoyama.newsviewer.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import com.example.yokoyama.newsviewer.data.Language
import com.example.yokoyama.newsviewer.R
import kotlinx.android.synthetic.main.radio_button_list_item.view.*

class LanguageAdapter(var checkedPosition: Int) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    val languages = Language.values()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.radio_button_list_item, parent, false))

    override fun getItemCount(): Int = languages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewItem.text = languages[position].displayName
        holder.radioButtonItem.isChecked = position == checkedPosition
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioButtonItem : RadioButton = itemView.radioButtonItem
        val textViewItem : TextView = itemView.textViewItemLabel

        init {
            radioButtonItem.setOnClickListener {
                checkedPosition = adapterPosition
                notifyDataSetChanged()
            }
        }
    }

}






