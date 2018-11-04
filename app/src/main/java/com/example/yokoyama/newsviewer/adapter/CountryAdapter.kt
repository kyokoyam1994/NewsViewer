package com.example.yokoyama.newsviewer.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import com.example.yokoyama.newsviewer.R
import com.example.yokoyama.newsviewer.data.Country
import kotlinx.android.synthetic.main.radio_button_list_item.view.*

class CountryAdapter(var checkedPosition: Int) : RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    val countries = Country.values()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.radio_button_list_item, parent, false))

    override fun getItemCount(): Int = countries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewItem.text = countries[position].displayName
        holder.radioButtonItem.isChecked = position == checkedPosition
        holder.imageViewItem.visibility = View.VISIBLE
        countries[position].imageResource?.let { holder.imageViewItem.setImageResource(it) }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioButtonItem : RadioButton = itemView.radioButtonItem
        val textViewItem : TextView = itemView.textViewItemLabel
        val imageViewItem : ImageView = itemView.imageViewItem

        init {
            radioButtonItem.setOnClickListener {
                checkedPosition = adapterPosition
                notifyDataSetChanged()
            }
        }
    }

}






