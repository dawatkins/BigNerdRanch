package com.example.geoquiz

import android.app.Activity
import android.content.Intent
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

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var cheatButton: Button
    private var totalCorrect = 0.0

    private val quizViewModel: QuizViewModel by lazy{
        of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate(Bundle?) called")

        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.previous_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatButton = findViewById(R.id.cheat_button)

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

//        questionTextView.setOnClickListener{
//            quizViewModel.moveToNext()
//            updateQuestion()
//        }

        cheatButton.setOnClickListener{
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
//            val intent = Intent(this, CheatActivity::class.java)
//            startActivity(intent)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }

        updateQuestion()
    }// on create

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

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

    override fun onSaveInstanceState(savedInstanceState: Bundle){
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
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
        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        if(messageResId == R.string.correct_toast){
            quizViewModel.setUserAnswer(1)
        } else{
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
