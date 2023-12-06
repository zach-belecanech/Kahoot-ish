package com.bignerdranch.android.kahoot_ish

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ScoreboardFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userScores: MutableList<UserScore>
    private lateinit var adapter: UserScoreAdapter
    private lateinit var roundView: TextView
    private lateinit var nextRoundView: TextView
    private lateinit var homeButton: Button
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_scoreboard, container, false)

        recyclerView = view.findViewById(R.id.scoreboardRecyclerView)
        roundView = view.findViewById(R.id.roundTextView)
        nextRoundView = view.findViewById(R.id.nextRoundTextView)
        homeButton = view.findViewById(R.id.returnToHomeButton)
        homeButton.visibility = View.GONE
        recyclerView.layoutManager = LinearLayoutManager(context)
        userScores = mutableListOf()
        adapter = UserScoreAdapter(userScores)
        recyclerView.adapter = adapter
        val roundNum = arguments?.getInt("questionNum") ?: 0
        roundView.text = "Round ${roundNum + 1}"
        loadDataFromFirebase()

        if (arguments?.getBoolean("gameDone") == true) {
            nextRoundView.text = "Game Over!"
            homeButton.visibility = View.VISIBLE
            homeButton.setOnClickListener {
                updateFirebaseVariable()
                findNavController().navigate(R.id.action_scoreboardFragment_to_homeFragment)
            }
            return view
        }
        startCountdown()

        return view
    }

    private fun startCountdown() {
        countDownTimer = object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                nextRoundView.text = "Next round in: ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                nextRoundView.text = "Starting next round!"
                var questionNum = arguments?.getInt("questionNum") ?: 0
                questionNum += 1
                // You can add any actions here that should happen after the countdown finishes
                findNavController().navigate(R.id.action_scoreboardFragment_to_readyScreenFragment, bundleOf("user_id" to arguments?.getString("user_id"),
                    "questionNum" to questionNum, "score" to arguments?.getInt("score")))
            }
        }.start()
    }

    private fun loadDataFromFirebase() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userScores.clear()
                for (postSnapshot in snapshot.children) {
                    val userScore = postSnapshot.getValue(UserScore::class.java)
                    userScore?.let { userScores.add(it) }
                }
                userScores.sortByDescending { it.score }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }

    private fun updateFirebaseVariable() {
        val database = FirebaseDatabase.getInstance()
        val gameStarted = database.getReference("gameStarted")
        val userRef = database.getReference("users")
        val questionRef = database.getReference("questions")
        val roomCreated = database.getReference("roomCreated")

        userRef.removeValue()
        gameStarted.setValue(false)
        questionRef.removeValue()
        roomCreated.setValue(false)

    }
}
