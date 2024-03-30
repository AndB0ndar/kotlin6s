package com.example.kotlin6s.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin6s.R


class QueueListAdapter(private val queueList: MutableList<String>) : RecyclerView.Adapter<QueueListAdapter.QueueViewHolder>() {
    private var onClickListener: OnClickListener? = null

    class QueueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewLabel: TextView = itemView.findViewById(R.id.textViewLabel)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_queue, parent, false)
        return QueueViewHolder(view)
    }

    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
        holder.textViewLabel.text = queueList[position]

        holder.buttonDelete.setOnClickListener {
            queueList.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return queueList.size
    }

    // A function to bind the onclickListener.
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int)
    }
}