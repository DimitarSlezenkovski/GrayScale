package com.example.grayscale3.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grayscale.R

class LabsAdapter(private var items: List<String>, private val listener: ClickedItemLabs): RecyclerView.Adapter<LabsAdapter.ViewHolder>() {
   inner class ViewHolder(view:View): RecyclerView.ViewHolder(view), View.OnClickListener {
        val laboratory = view.findViewById<TextView>(R.id.Labaratory)
       init {
           itemView.setOnClickListener(this)
       }
       override fun onClick(p0: View?) {
           val position = adapterPosition
           val item = items
           if(position != RecyclerView.NO_POSITION){
               listener.getItemClick(position, item)
           }
       }
   }

    interface ClickedItemLabs{
        fun getItemClick(position: Int, items: List<String>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.labs_recycler_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder.laboratory.text = item
    }

    override fun getItemCount(): Int {
       return items.size
    }
    fun filterList(filteredItems: List<String>){
        items = filteredItems
        notifyDataSetChanged()
    }
}