package com.example.grayscale3.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grayscale.R

class ChildRecyclerView(private var cities: List<String>, private val listener: CityClick): RecyclerView.Adapter<ChildRecyclerView.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{
        val citiesTV = view.findViewById<TextView>(R.id.CityInCountry)

        init {
            itemView.setOnClickListener(this)
        }


        override fun onClick(p0: View?) {
            val position = adapterPosition
            val cities = cities
            if(position != RecyclerView.NO_POSITION){
                listener.getCityClicked(position, cities)
            }

        }


    }



    interface CityClick{
        fun getCityClicked(position: Int, cities: List<String>){}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cities_in_the_country, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cities.get(position)
       // Log.e("item", "$item")
        holder.citiesTV.text = item
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    fun filterList(filteredList: List<String>){
        cities = filteredList
        notifyDataSetChanged()
    }
}