package com.example.grayscale.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grayscale.R

class TestAdapter(private val items: List<String>, private val listener: ItemClick): RecyclerView.Adapter<TestAdapter.ViewHolder>() {
   inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
       var testTV = view.findViewById<TextView>(R.id.Test)!!
       init {
           itemView.setOnClickListener(this)
       }
       override fun onClick(p0: View?) {
           val position = adapterPosition
           val items = items
           if(position != RecyclerView.NO_POSITION){
               listener.getItemClicked(position, items)
           }
       }
   }

    interface ItemClick{
        fun getItemClicked(position: Int, items: List<String>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.tests_recycler_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder.testTV.text = item
    }

    override fun getItemCount(): Int {
        return items.size
    }
}