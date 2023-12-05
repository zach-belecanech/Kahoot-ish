package com.bignerdranch.android.kahoot_ish

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsersAdapter(private val users: List<String>) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    class UserViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(userName: String) {
            view.findViewById<TextView>(R.id.userNameTextView).text = userName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size
}
