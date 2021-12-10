package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val postList:ArrayList<Post>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = postList[position]

        holder.postItemTitle.text = currentItem.title
        holder.postItemEmail.text = currentItem.userEmail
        holder.postItemContent.text = currentItem.details
        holder.postItemReply.text = currentItem.reply
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class MyViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val postItemTitle : TextView = itemView.findViewById(R.id.postItemTitle)
        val postItemEmail : TextView = itemView.findViewById(R.id.postItemEmail)
        val postItemContent : TextView = itemView.findViewById(R.id.postItemContent)
        val postItemReply : TextView = itemView.findViewById(R.id.postItemReply)
    }
}