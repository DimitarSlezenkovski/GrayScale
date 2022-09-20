package com.example.grayscale3.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grayscale.R

class PatientAdapter(private val patients: List<String>?, private val listener: ItemClickedPatients) : RecyclerView.Adapter<PatientAdapter.ViewHolder>(){
   inner class ViewHolder(view:View): RecyclerView.ViewHolder(view), View.OnClickListener {
        val _patients = view.findViewById<TextView>(R.id.Patients)
       init {
           itemView.setOnClickListener(this)
       }
       override fun onClick(p0: View?) {
           val position = adapterPosition
           val patient = patients
           if(position != RecyclerView.NO_POSITION){
               listener.getItemClicked(position, patient!!)
           }
       }
   }
    interface ItemClickedPatients{
        fun getItemClicked(position: Int, patients: List<String>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.patients_recycler_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = patients?.get(position)
        holder._patients.text = item
    }

    override fun getItemCount(): Int {
        return patients?.size!!
    }
}