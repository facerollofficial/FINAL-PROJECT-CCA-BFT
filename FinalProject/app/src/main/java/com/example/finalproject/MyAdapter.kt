package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val postList:ArrayList<Post>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_item,parent,false)

        return MyViewHolder(itemView,mListener)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = postList[position]

        holder.postItemTitle.text = currentItem.title
        holder.postItemEmail.text = currentItem.userEmail
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class MyViewHolder(itemView : View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView){
        val postItemTitle : TextView = itemView.findViewById(R.id.postItemTitle)
        val postItemEmail : TextView = itemView.findViewById(R.id.postItemEmail)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }
}