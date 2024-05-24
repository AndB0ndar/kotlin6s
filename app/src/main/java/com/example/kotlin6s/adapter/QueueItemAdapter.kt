package com.example.kotlin6s.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin6s.R
import com.example.kotlin6s.model.api.QueueItemResponse


class QueueItemAdapter(private val originalQueueList: MutableList<QueueItemResponse>) : RecyclerView.Adapter<QueueItemAdapter.QueueViewHolder>() {

    private var filteredQueueList: MutableList<QueueItemResponse> = originalQueueList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pos, parent, false)
        return QueueViewHolder(view)
    }

    override fun getItemCount(): Int = filteredQueueList.size

    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
        val currentItem = filteredQueueList[position]
        holder.bind(currentItem)
    }

    inner class QueueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewLabel: TextView = itemView.findViewById(R.id.textViewLabel)
        private val textPosition: TextView = itemView.findViewById(R.id.textPosition)

        fun bind(queue: QueueItemResponse) {
            textViewLabel.text = queue.userLogin
            textPosition.text = queue.position.toString()
        }
    }

    fun filter(query: String) {
        filteredQueueList = if (query.isEmpty()) {
            originalQueueList.toMutableList()
        } else {
            originalQueueList.filter { it.userLogin.contains(query, ignoreCase = true) }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun clear() {
        filteredQueueList = originalQueueList.toMutableList()
        notifyDataSetChanged()
    }
}

