package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(private val userList: ArrayList<Student>):
    RecyclerView.Adapter<UserAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item,
        parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = userList[position]

        holder.infullname.text = currentitem.fullname
        holder.inemail.text = currentitem.email
        holder.inprogram.text = currentitem.program
        holder.inyear.text = currentitem.yearLevel
    }

    override fun getItemCount(): Int {
        return userList.size
    }



    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val infullname : TextView = itemView.findViewById(R.id.tvFullname)
        val inemail : TextView = itemView.findViewById(R.id.tvEmail)
        val inprogram : TextView = itemView.findViewById(R.id.tvProgram)
        val inyear : TextView = itemView.findViewById(R.id.tvYearLevel)
    }
}