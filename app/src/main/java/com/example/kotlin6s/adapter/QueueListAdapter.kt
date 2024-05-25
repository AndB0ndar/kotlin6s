package com.example.kotlin6s.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin6s.R
import com.example.kotlin6s.model.api.Queue
import androidx.recyclerview.widget.DiffUtil

class QueueListAdapter(private val originalQueueList: MutableList<Queue>, private val token: String?) : RecyclerView.Adapter<QueueListAdapter.QueueViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null
    private var filteredQueueList: MutableList<Queue> = originalQueueList.toMutableList()

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Queue>() {
            override fun areItemsTheSame(oldItem: Queue, newItem: Queue): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Queue, newItem: Queue): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_queue, parent, false)
        return QueueViewHolder(itemView)
    }

    override fun getItemCount(): Int = filteredQueueList.size

    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
        val currentItem = filteredQueueList[position]
        holder.bind(currentItem)
    }

    fun filter(query: String) {
        filteredQueueList = if (query.isEmpty()) {
            originalQueueList.toMutableList()
        } else {
            originalQueueList.filter { it.queueName.contains(query, ignoreCase = true) }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun clear() {
        filteredQueueList = originalQueueList.toMutableList()
        notifyDataSetChanged()
    }

    inner class QueueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewQueueName: TextView = itemView.findViewById(R.id.textViewQueueName)
        private val textQueueId: TextView = itemView.findViewById(R.id.queueId)
        private val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val queue = filteredQueueList[position]
                    onItemClickListener?.onItemClick(queue)
                }
            }
            buttonDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val queue = filteredQueueList[position]
                    if (queue.creatorToken == token) {
                        onItemClickListener?.onButtonDelete(queue, position)
                    } else {
                        buttonDelete.visibility = View.INVISIBLE
                    }
                }
            }
        }

        fun bind(queue: Queue) {
            textViewQueueName.text = queue.queueName
            textQueueId.text = queue.id.toString()
        }
    }

    fun setOnClickListener(onClickListener: OnItemClickListener) {
        this.onItemClickListener = onClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(queue: Queue)
        fun onButtonDelete(queue: Queue, position: Int)
    }
}

