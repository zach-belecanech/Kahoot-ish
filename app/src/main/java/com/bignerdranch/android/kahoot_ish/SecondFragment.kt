package com.bignerdranch.android.kahoot_ish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.kahoot_ish.databinding.FragmentSecondBinding
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val uniqueUserId = UUID.randomUUID().toString()
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

            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users")


            val userInfo = mapOf(
                "name" to playerName,
                "questions" to listOf(
                    mapOf("question" to q1, "answer" to q1_ans),
                    mapOf("question" to q2, "answer" to q2_ans)
                )
            )
            userRef.child(uniqueUserId).setValue(userInfo)

            findNavController().navigate(R.id.action_SecondFragment_to_lobbyFragment, bundleOf("userRole" to uniqueUserId))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

