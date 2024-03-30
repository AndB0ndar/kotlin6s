package com.example.kotlin6s.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin6s.R


class QueueAdapter(private val queueList: MutableList<String>) :
    RecyclerView.Adapter<QueueAdapter.QueueViewHolder>() {

    class QueueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewLabel: TextView = itemView.findViewById(R.id.textViewLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueAdapter.QueueViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pos, parent, false)
        return QueueAdapter.QueueViewHolder(view)
    }

    override fun onBindViewHolder(holder: QueueAdapter.QueueViewHolder, position: Int) {
        holder.textViewLabel.text = queueList[position]
    }

    override fun getItemCount(): Int {
        return queueList.size
    }
}
