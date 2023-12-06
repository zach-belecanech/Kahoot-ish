package com.bignerdranch.android.kahoot_ish

import android.R.layout
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ReadyScreenFragment : Fragment() {
    private var countdownText: TextView? = null
    private val handler: Handler = Handler()
    private var countdownNumber = 5
    private var questionBank: List<Question> = emptyList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        loadQuestionsFromFirebase()
        val view: View = inflater.inflate(R.layout.ready_screen, container, false)
        countdownText = view.findViewById(R.id.countdownTextView) // Replace with your TextView's ID
        startCountdown()
        return view
    }

    private fun startCountdown() {
        val countdownRunnable: Runnable = object : Runnable {
            override fun run() {
                if (countdownNumber > 0) {
                    countdownText!!.text = countdownNumber.toString()
                    countdownNumber--
                    handler.postDelayed(this, 1000)
                } else {
                    goToNextFragment()
                }
            }
        }
        handler.postDelayed(countdownRunnable, 1000)
    }

    private fun goToNextFragment() {
        // Replace with your code to transition to the next fragment
        findNavController().navigate(R.id.action_readyScreenFragment_to_questionFragment,
            bundleOf("questions" to questionBank.toTypedArray(),
                "user_id" to arguments?.getString("user_id").toString(),
                "questionNum" to arguments?.getInt("questionNum"), "score" to arguments?.getInt("score")))
    }

    private fun loadQuestionsFromFirebase() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("questions")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val questions = mutableListOf<Question>()
                for (questionSnapshot in snapshot.children) {
                    val question = questionSnapshot.getValue(Question::class.java)
                    question?.let { questions.add(it) }
                }
                questionBank = questions
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show()
            }
        })

    }

}
