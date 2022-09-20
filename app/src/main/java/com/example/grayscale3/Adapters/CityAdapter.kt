package com.example.grayscale3.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blongho.country_data.World
import com.example.grayscale.R
import com.example.grayscale3.RequestAndResponse.IntakeFullKit.CitiesRequest
import java.util.*
import kotlin.collections.ArrayList


class CityAdapter(private var items: List<CitiesRequest>, private val context: Context, private val listener: ItemClicked): RecyclerView.Adapter<CityAdapter.ViewHolder>(),
    ChildRecyclerView.CityClick {

    private lateinit var adapter: ChildRecyclerView
    private var citiesList: List<String> = ArrayList()
    var collapsed = false
    val newItems = items.groupBy({it.country!!}, {it.city!!})
    var cities: List<String> = listOf()
    private val world = World.init(context)
    private val layoutDisabledScroll = object : LinearLayoutManager(context){
        override fun canScrollVertically(): Boolean {
            return false
        }
    }
    private val layout = LinearLayoutManager(context)

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val countryFlag  = view.findViewById<ImageView>(R.id.CountryFlag)
        val country = view.findViewById<TextView>(R.id.CountryName)
        val childRecyclerView = view.findViewById<RecyclerView>(R.id.childRecyclerView)
        val collapse = view.findViewById<LinearLayout>(R.id.Collapsable)
        val collapsableRelative = view.findViewById<RelativeLayout>(R.id.CollapsableRelative)
        val ImageNameLinearLayout = view.findViewById<LinearLayout>(R.id.linearLayout)

    }

    interface ItemClicked{
        fun countryClick(position: Int, items: List<CitiesRequest>, recyclerView: RecyclerView){}
        fun cityClick(position: Int, items: List<String>){}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.city_recycler_view_items, parent, false))
    }


    fun recyclerViewDeco(holder: ViewHolder){
        holder.childRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                layout.orientation
            )
        )
        holder.childRecyclerView.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
    }

    private fun countries(): ArrayList<String>{
        val country = arrayListOf<String>()
        newItems.entries.forEach {
            country.add(it.key)
        }
        Log.e("countries it", "$country")
        return country
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var country = ""
        val newVal = newItems.filter{
            it.key.isNotEmpty() && !it.value.isNullOrEmpty()
        }
            if (position <= newVal.size -1){
                country = countries()[position]
                holder.country.text = country
            }

            if (newVal.containsKey(country)) {
                cities = newVal[country]!!
                citiesList = cities
                cityRecyclerView(holder, cities)
                World.getFlagOf(country).let {
                    holder.countryFlag.setImageResource(it)
                }
            }



    }

    private fun cityRecyclerView(holder: ViewHolder, list: List<String>){
        adapter = ChildRecyclerView(list, this)
        recyclerViewDeco(holder)
        holder.childRecyclerView.adapter = adapter
    }


    fun filterChildList(list: List<String>){
        adapter.filterList(list)
    }
    fun filterList(filteredItems: List<CitiesRequest>){
        items = filteredItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return newItems.size
    }

    override fun getCityClicked(position: Int, cities: List<String>) {
        listener.cityClick(position, cities)
    }
}