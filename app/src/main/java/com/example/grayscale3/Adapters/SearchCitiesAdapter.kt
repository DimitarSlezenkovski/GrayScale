package com.example.grayscale3.Adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grayscale3.RequestAndResponse.IntakeFullKit.CitiesRequest


class SearchCitiesAdapter(
    private var items: List<CitiesRequest>,
    private val context: Context
): RecyclerView.Adapter<SearchCitiesAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
       return items.size
    }
}