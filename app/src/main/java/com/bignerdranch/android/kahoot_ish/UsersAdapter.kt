package com.bignerdranch.android.kahoot_ish

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsersAdapter(private val users: List<LobbyFragment.User>) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    // Define the ViewHolder
    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.userNameTextView)

        fun bind(user: LobbyFragment.User) {
            nameTextView.text = user.name ?: "Unknown"  // Handle null case
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        // Create a new view from the layout
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the contents of the view
        val user = users[position]
        holder.bind(user)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = users.size
}

