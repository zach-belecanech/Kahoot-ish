package com.bignerdranch.android.kahoot_ish

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase

class QuestionFragment : Fragment() {
    private val TAG = "QuestionFragment"
    private var countdownNumber = 15
    private val handler = Handler()
    private lateinit var countdownTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var option1: Button
    private lateinit var option2: Button
    private lateinit var option3: Button
    private lateinit var option4: Button
    private lateinit var optionChosen: Button
    private var score: Int = 0
    private var questionNum = arguments?.getInt("questionNum") ?: 0
    private var questions: List<Question>? = null
    private var gameDone = arguments?.getBoolean("gameDone") ?: false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_question, container, false)
        countdownTextView = view.findViewById(R.id.countdownTextView)
        questionTextView = view.findViewById(R.id.questionTextView)
        option1 = view.findViewById(R.id.optionButton1)
        option2 = view.findViewById(R.id.optionButton2)
        option3 = view.findViewById(R.id.optionButton3)
        option4 = view.findViewById(R.id.optionButton4)
        
        if (arguments?.getString("user_id") == "host") {
            option1.isEnabled = false
            option2.isEnabled = false
            option3.isEnabled = false
            option4.isEnabled = false
        } else {
            option1.isEnabled = true
            option2.isEnabled = true
            option3.isEnabled = true
            option4.isEnabled = true
        }


        questions = arguments?.getParcelableArray("questions")?.map { it as Question }
        score = arguments?.getInt("score") ?: 0
        questionNum = arguments?.getInt("questionNum") ?: 0
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (questions?.getOrNull(questionNum+1) == null) {
            gameDone = true
        }
        questions?.getOrNull(questionNum)?.let { question ->
            questionTextView.text = question.question

            var incorrectAnswers = if (question.incorrect != "") {
                question.incorrect.split(",")
            } else {
                listOf<String>("No incorrect answers found.", "No incorrect answers found.", "No incorrect answers found.")
            }
            val correctAnswer = question.answer
            val allAnswers = incorrectAnswers + correctAnswer
            val shuffledAnswers = allAnswers.shuffled()
            option1.text = shuffledAnswers[0]
            option2.text = shuffledAnswers[1]
            option3.text = shuffledAnswers[2]
            option4.text = shuffledAnswers[3]
            startCountdown()
        }
        optionChosen = option1
        option1.setOnClickListener {
            if (option1.text == questions?.getOrNull(questionNum)?.answer) {
                score += (countdownNumber * 10)
            }
            option1.isClickable = false
            option2.isEnabled = false
            option3.isEnabled = false
            option4.isEnabled = false
            optionChosen = option1
        }
        option2.setOnClickListener {
            if (option2.text == questions?.getOrNull(questionNum)?.answer) {
                score += (countdownNumber * 10)
            }
            option1.isEnabled = false
            option2.isClickable = false
            option3.isEnabled = false
            option4.isEnabled = false
            optionChosen = option2
        }
        option3.setOnClickListener {
            if (option3.text == questions?.getOrNull(questionNum)?.answer) {
                score += (countdownNumber * 10)
            }
            option1.isEnabled = false
            option2.isEnabled = false
            option3.isClickable = false
            option4.isEnabled = false
            optionChosen = option3
        }
        option4.setOnClickListener {
            if (option4.text == questions?.getOrNull(questionNum)?.answer) {
                score += (countdownNumber * 10)
            }
            option1.isEnabled = false
            option2.isEnabled = false
            option3.isEnabled = false
            option4.isClickable = false
            optionChosen = option4
        }
    }

    private fun startCountdown() {
        val countdownRunnable = object : Runnable {
            override fun run() {
                if (countdownNumber > 0) {
                    countdownTextView.text = countdownNumber.toString()
                    countdownNumber--
                    handler.postDelayed(this, 1000)
                } else {
                    countdownTextView.text = "Time's up!"
                    displayCorrectAnswer()
                }
            }
        }
        handler.postDelayed(countdownRunnable, 1000)
    }

    private fun displayCorrectAnswer() {
        // Update this method to display the correct answer
        val correctAnswer = questions?.getOrNull(questionNum)?.answer ?: "Unknown"
        questionTextView.text = "Answer: $correctAnswer"

        if (optionChosen.text == correctAnswer) {
            optionChosen.setBackgroundColor(android.graphics.Color.GREEN)
        } else {
            optionChosen.setBackgroundColor(android.graphics.Color.RED)
        }
        setScore()// Updates the score in the database
        handler.postDelayed({
            goToNextFragment()
        }, 10000) // 10-second delay before navigating
    }

    private fun goToNextFragment() {
        // Navigate to the next fragment
        findNavController().navigate(R.id.action_questionFragment_to_scoreboardFragment,
            bundleOf("user_id" to arguments?.getString("user_id").toString(),
                "questionNum" to questionNum, "score" to score, "gameDone" to gameDone)) // Replace with actual action ID
    }

    private fun setScore() {
        val user_id = arguments?.getString("user_id").toString()
        if (user_id == "host") {
            return
        }
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users/$user_id/score")
        myRef.setValue(score)
    }
}
