package com.example.kotlin6s.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin6s.R


class QueueAdapter(private val queueList: MutableList<String>) : RecyclerView.Adapter<QueueAdapter.QueueViewHolder>() {
    private val filteredList: MutableList<String> = mutableListOf()
    init {
        filteredList.addAll(queueList)
    }

    class QueueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewLabel: TextView = itemView.findViewById(R.id.textViewLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pos, parent, false)
        return QueueViewHolder(view)
    }

    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
        val item = filteredList[position]
        holder.textViewLabel.text = item
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun clear() {
        filteredList.addAll(queueList)
    }

    fun filter(query: String) {
        filteredList.clear()
        if (query.isNotEmpty()) {
            val queryLowercase = query.lowercase()
            for (item in queueList) {
                if (item.lowercase().contains(queryLowercase)) {
                    filteredList.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }
}
