package com.example.firebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val data: ArrayList<UserItemData>) : RecyclerView.Adapter<Adapter.UserDataViewHolder>() {

    class UserDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val t1: TextView = itemView.findViewById(R.id.t1)
        val t2: TextView = itemView.findViewById(R.id.t2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return UserDataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserDataViewHolder, position: Int) {
        val item = data[position]
        holder.t1.text = item.txt1
        holder.t2.text = item.txt2
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
