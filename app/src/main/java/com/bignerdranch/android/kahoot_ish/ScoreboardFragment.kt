package com.bignerdranch.android.kahoot_ish

import android.content.ContentValues
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
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ScoreboardFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userScores: MutableList<UserScore>
    private lateinit var adapter: UserScoreAdapter
    private lateinit var roundView: TextView
    private lateinit var nextRoundView: TextView
    private lateinit var homeButton: Button
    private var countDownTimer: CountDownTimer? = null
    private var userChangesListener: ValueEventListener? = null
    private lateinit var saveScreenshotButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_scoreboard, container, false)

        recyclerView = view.findViewById(R.id.scoreboardRecyclerView)
        roundView = view.findViewById(R.id.roundTextView)
        nextRoundView = view.findViewById(R.id.nextRoundTextView)
        homeButton = view.findViewById(R.id.returnToHomeButton)
        saveScreenshotButton = view.findViewById(R.id.saveScreenshotButton)
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
            saveScreenshotButton.visibility = View.VISIBLE
            saveScreenshotButton.setOnClickListener {
                takeScreenshotAndSave()
            }
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
        userChangesListener = (object : ValueEventListener {
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
        databaseReference.addValueEventListener(userChangesListener!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val database = FirebaseDatabase.getInstance()
        database.getReference("users").removeEventListener(userChangesListener!!)
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

    private fun takeScreenshotAndSave() {
        val bitmap = takeScreenshot()
        saveImageToGallery(bitmap)
    }

    private fun takeScreenshot(): Bitmap {
        val rootView = activity?.window?.decorView?.rootView
        val bitmap = Bitmap.createBitmap(rootView!!.width, rootView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        rootView.draw(canvas)
        return bitmap
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val filename = "screenshot_${Calendar.getInstance().timeInMillis}.png"
        val fos: FileOutputStream
        val imageUri: Uri
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        // Inserting the contentValues to contentResolver and getting the Uri
        val resolver = activity?.contentResolver
        imageUri = resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
        fos = (resolver.openOutputStream(imageUri) as FileOutputStream?)!!

        // Writing the bitmap to the Uri obtained
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()

        Toast.makeText(context, "Screenshot saved to Gallery", Toast.LENGTH_SHORT).show()
    }
}
