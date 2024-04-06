package com.example.kotlin6s.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin6s.R
import java.util.Locale


class QueueListAdapter(private val queueList: MutableList<String>) : RecyclerView.Adapter<QueueListAdapter.QueueViewHolder>() {
    private var onClickListener: OnClickListener? = null

    private val filteredList: MutableList<String> = mutableListOf()

    init {
        filteredList.addAll(queueList)
    }

    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
        val item = filteredList[position]
        holder.textViewLabel.text = item

        holder.buttonDelete.setOnClickListener {
            filteredList.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_queue, parent, false)
        return QueueViewHolder(view)
    }

    override fun getItemCount(): Int = filteredList.size

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(queueList)
        } else {
            val queryLowercase = query.lowercase()
            for (item in queueList) {
                if (item.lowercase().contains(queryLowercase)) {
                    filteredList.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }

    class QueueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewLabel: TextView = itemView.findViewById(R.id.textViewLabel)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)
    }
}
