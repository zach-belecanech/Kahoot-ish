package com.bignerdranch.android.kahoot_ish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.kahoot_ish.databinding.FragmentSecondBinding
import com.google.firebase.database.FirebaseDatabase

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonEnterGame.setOnClickListener {
            val playerName = arguments?.getString("playerName")
            val q1 = binding.q1TextBox.text.toString()
            val q2 = binding.q2TextBox.text.toString()
            val q1_ans = binding.q1TextBoxAns.text.toString()
            val q2_ans = binding.q2TextBoxAns.text.toString()


            findNavController().navigate(R.id.action_SecondFragment_to_lobbyFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class QuestionData(
    val playerName: String? = null,
    val question1: String? = null,
    val question2: String? = null,
    val answer1: String? = null,
    val answer2: String? = null
)