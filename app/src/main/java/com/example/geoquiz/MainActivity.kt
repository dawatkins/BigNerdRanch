package com.example.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders.*
import kotlin.math.round

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private var totalCorrect = 0.0

    private val quizViewModel: QuizViewModel by lazy{
        of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate(Bundle?) called")

        setContentView(R.layout.activity_main)

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.previous_button)
        questionTextView = findViewById(R.id.question_text_view)

        trueButton.setOnClickListener {
            checkAnswer(true)
            trueButton.isClickable = false
            falseButton.isClickable = false
        }
        falseButton.setOnClickListener {
            checkAnswer(false)
            falseButton.isClickable = false
            trueButton.isClickable = false
            }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            if(quizViewModel.currentIndex != 0)
                updateQuestion()
            else
                displayGrade()
            updateQuestion()
            falseButton.isClickable = true
            trueButton.isClickable = true
        }
        prevButton.setOnClickListener{
            quizViewModel.moveToPrevious()
            if (quizViewModel.currentIndex == -1)
                quizViewModel.moveToNext()
            updateQuestion()
            falseButton.isClickable = true
            trueButton.isClickable = true
        }

        questionTextView.setOnClickListener{
            quizViewModel.moveToNext()
            updateQuestion()
        }
        updateQuestion()
    }// on create

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }


    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId: String

        if(userAnswer == correctAnswer){
            messageResId = "Correct!"
            quizViewModel.setUserAnswer(1)
        }else{
            messageResId = "Incorrect!"
            quizViewModel.setUserAnswer(0)
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun displayGrade() {
        val questions = quizViewModel.size()
        totalCorrect = quizViewModel.correctAnswers()
        var gradePercent =((totalCorrect / questions) * 100)
        gradePercent = gradePercent.round(2)
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Quiz Grade")
            .setMessage("You got ${totalCorrect.toInt()} correct out of $questions.\nYour grade was: $gradePercent %")
            .create()
            .show()

        resetValues()
        updateQuestion()
    }

    private fun Double.round(decimals:Int) : Double{
        var multiplier = 1.0
        repeat(decimals) {multiplier *= 10}
        return round(this * multiplier) / multiplier
    }

    private fun resetValues(){
        totalCorrect = 0.0
    }

}
