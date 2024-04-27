package com.example.kotlin6s.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin6s.R


class SearchHistoryAdapter(postList: MutableList<String?>) : RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder>() {
    private var onClickListener: OnClickListener? = null
    private val historyList: MutableList<String?>

    init {
        this.historyList = postList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return SearchHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.textViewLabel.text = item
        holder.itemView.setOnClickListener {
            if (item != null) {
                onClickListener?.onClick(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: String)
    }

    inner class SearchHistoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textViewLabel: TextView = itemView.findViewById(com.example.kotlin6s.R.id.textViewLabel)
    }
}

