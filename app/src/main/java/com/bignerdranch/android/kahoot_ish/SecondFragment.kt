package com.bignerdranch.android.kahoot_ish

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.kahoot_ish.databinding.FragmentSecondBinding
import com.google.firebase.database.FirebaseDatabase
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val TAG = "SecondFragment"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val playerName = arguments?.getString("playerName")
    val uniqueUserId = UUID.randomUUID().toString()
    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Authorization", "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") // Replace YOUR_API_KEY with your actual API key
                .method(original.method, original.body)
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
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
            val q1 = binding.q1TextBox.text.toString()
            val q2 = binding.q1TextBoxAns.text.toString()
            val q1_ans = binding.q1TextBoxAns.text.toString()
            val q2_ans = binding.q2TextBoxAns.text.toString()
            Log.d(TAG, "The value of myValue is: $q1")
            val prompt1 = "Give three incorrect answers in a list for the question: $q1"
            askChatGpt(prompt1, q1, q2, q1_ans, q2_ans)
            findNavController().navigate(R.id.action_SecondFragment_to_lobbyFragment, bundleOf("userRole" to uniqueUserId))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun askChatGpt(question: String, q1: String, q2: String, q1_ans: String, q2_ans: String) {
        val service = retrofit.create(ChatGptService::class.java)
        val call = service.getAnswers(ChatGptRequest(question))
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users")

        call.enqueue(object : Callback<ChatGptResponse> {
            override fun onResponse(call: Call<ChatGptResponse>, response: Response<ChatGptResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        // Handle the response here. Extract the choices and display them.
                        val userInfo = mapOf(
                            "name" to playerName,
                            "questions" to listOf(
                                mapOf("question" to q1, "answer" to q1_ans, "incorrect" to body.toString()),
                                mapOf("question" to q2, "answer" to q2_ans)
                            )
                        )
                        userRef.child(uniqueUserId).setValue(userInfo)
                    }
                } else {
                    // Handle API errors
                    val userInfo = mapOf(
                        "name" to playerName,
                        "questions" to listOf(
                            mapOf("question" to q1, "answer" to q1_ans, "incorrect" to "Error"),
                            mapOf("question" to q2, "answer" to q2_ans)
                        )
                    )
                    userRef.child(uniqueUserId).setValue(userInfo)
                }
            }

            override fun onFailure(call: Call<ChatGptResponse>, t: Throwable) {
                // Handle network errors
            }
        })
    }



}

