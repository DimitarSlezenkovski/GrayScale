package com.example.grayscale3.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grayscale.R

class AwbNumbersAdapter(private val numbers: List<String>, private val listener: GetItemThatIsClicked): RecyclerView.Adapter<AwbNumbersAdapter.ViewHolder>() {
    inner class ViewHolder(view : View): RecyclerView.ViewHolder(view), View.OnClickListener {
        val numberDisplay = view.findViewById<TextView>(R.id.Numbers)!!

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            val numbers = numbers
            if(position != RecyclerView.NO_POSITION){
                listener.getItemClicked(position, numbers)
            }
        }
    }

    interface GetItemThatIsClicked{
        fun getItemClicked(position: Int, items: List<String>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.awb_numbers_adapter, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val number =  numbers.get(position)
        holder.numberDisplay.text = number
    }

    override fun getItemCount(): Int {
        return numbers.size
    }
}