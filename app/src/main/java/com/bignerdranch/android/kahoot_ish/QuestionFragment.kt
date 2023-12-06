package com.bignerdranch.android.kahoot_ish

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

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

    private var questions: List<Question>? = null

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


        questions = arguments?.getParcelableArray("questions")?.map { it as Question }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        questions?.firstOrNull()?.let { question ->
            questionTextView.text = question.question
            val incorrectAnswers = question.incorrect.split(",")
            val correctAnswer = question.answer
            val allAnswers = incorrectAnswers + correctAnswer
            val shuffledAnswers = allAnswers.shuffled()
            option1.text = shuffledAnswers[0]
            option2.text = shuffledAnswers[1]
            option3.text = shuffledAnswers[2]
            option4.text = shuffledAnswers[3]

            startCountdown()
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
                    displayCorrectAnswer()
                }
            }
        }
        handler.postDelayed(countdownRunnable, 1000)
    }

    private fun displayCorrectAnswer() {
        // Update this method to display the correct answer
        val correctAnswer = questions?.firstOrNull()?.answer ?: "Unknown"
        questionTextView.text = "Answer: $correctAnswer"

        handler.postDelayed({
            goToNextFragment()
        }, 10000) // 10-second delay before navigating
    }

    private fun goToNextFragment() {
        // Navigate to the next fragment
        findNavController().navigate(R.id.action_questionFragment_to_scoreboardFragment) // Replace with actual action ID
    }
}
