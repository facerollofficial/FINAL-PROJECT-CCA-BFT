package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppointmentAdapter(private val appointmentList: ArrayList<Appointment>):

    RecyclerView.Adapter<AppointmentAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.appointment_item,
                parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val currentitem = appointmentList[position]

            holder.title.text = currentitem.eventTitle
            holder.sender.text = currentitem.sender
            holder.venue.text = currentitem.venue
            holder.start.text = currentitem.startTime
            holder.end.text = currentitem.endTime
            holder.date.text = currentitem.date
            holder.recipient.text = currentitem.recipient
        }

        override fun getItemCount(): Int {
            return appointmentList.size
        }



        class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val title : TextView = itemView.findViewById(R.id.appointmentItemTitle)
            val sender : TextView = itemView.findViewById(R.id.appointmentItemEmail)
            val venue : TextView = itemView.findViewById(R.id.appointmentItemVenue)
            val start : TextView = itemView.findViewById(R.id.appointmentItemStart)
            val end : TextView = itemView.findViewById(R.id.appointmentItemEnd)
            val date : TextView = itemView.findViewById(R.id.appointmentItemDate)
            val recipient : TextView = itemView.findViewById(R.id.appointmentItemRecipient)
        }
}