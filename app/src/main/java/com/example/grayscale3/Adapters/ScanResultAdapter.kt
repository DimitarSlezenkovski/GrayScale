package com.example.grayscale3.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grayscale.R
import com.example.grayscale3.RequestAndResponse.Patients.BarcodeAndName

class ScanResultAdapter(private var items: List<BarcodeAndName>): RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val barcode = view.findViewById<TextView>(R.id.Barcode)!!
        val name = view.findViewById<TextView>(R.id.Name)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.scan_result_recycler_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder.barcode.text = "Barcode: ${item.Barcode}"
        if(item.Name.isNullOrEmpty()){
            holder.name.text = ""
        }
        holder.name.text = "Name: ${item.Name}"
    }

    override fun getItemCount(): Int {
        return items.size
    }
}