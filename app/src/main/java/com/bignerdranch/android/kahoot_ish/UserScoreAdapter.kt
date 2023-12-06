package com.bignerdranch.android.kahoot_ish

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserScoreAdapter(private val userScores: List<UserScore>) : RecyclerView.Adapter<UserScoreAdapter.UserScoreViewHolder>() {

    class UserScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.usernameTextView)
        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserScoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_scoreboard, parent, false)
        return UserScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserScoreViewHolder, position: Int) {
        val userScore = userScores[position]
        holder.nameTextView.text = userScore.name
        holder.scoreTextView.text = userScore.score.toString()
    }

    override fun getItemCount() = userScores.size
}
