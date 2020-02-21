package com.example.geoquiz

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders.*
import com.example.geoquiz.api.ApiService
import com.example.geoquiz.api.model.ApiQuestion
import com.example.geoquiz.api.model.QuestionList
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import us.bndshop.geoquiz.api.RestAPIClient
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


    private var questions = mutableListOf<ApiQuestion>()
    private var answer = false
    private var correctAnswer = true
    private var question: ApiQuestion? = null
    private var index = 0

    private val quizViewModel: QuizViewModel by lazy {
        of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        apiService = apiClient.getApiService()

        Log.d(TAG, "onCreate(Bundle?) called")

        if (savedInstanceState != null) {
            totalCorrect = savedInstanceState.getDouble("CORRECT")
            index = savedInstanceState.getInt("INDEX")
            answer = savedInstanceState.getBoolean("ANSWER")
            questions = savedInstanceState.getParcelableArrayList<ApiQuestion>("QUESTION_LIST")?.toMutableList()!!
            setupQuestion()
        } else {
            getQuestions()
        }


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
            if (index < questions.size - 1) {
                getQuestion(index++)
                setupQuestion()
            } else {
                displayGrade()
                resetValues()
            }
            setupQuestion()
            falseButton.isClickable = true
            trueButton.isClickable = true
        }
        prevButton.setOnClickListener {
            if (index > 0) {
                getQuestion(index--)
                setupQuestion()
            } else {
                Toast.makeText(applicationContext, "First question in the list.", Toast.LENGTH_LONG)
                    .show()
            }
            falseButton.isClickable = true
            trueButton.isClickable = true
        }


        cheatButton.setOnClickListener { view ->
            val answer = question?.correctAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answer)
            startActivity(intent)
        }
    }// on create

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
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

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        savedInstanceState?.putDouble("CORRECT", totalCorrect)
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
        questionTextView.text = question?.question
    }




    private fun checkAnswer(userAnswer: Boolean) {
//        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        if (messageResId == R.string.correct_toast)
            totalCorrect++


        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun displayGrade() {
//        val questions = quizViewModel.size()
//        totalCorrect = quizViewModel.correctAnswers()
        var gradePercent = ((totalCorrect / NUMBER_OF_QUESTIONS) * 100)
        gradePercent = gradePercent.round(2)
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Quiz Grade")
            .setMessage("You got ${totalCorrect.toInt()} correct out of $NUMBER_OF_QUESTIONS.\nYour grade was: $gradePercent %")
            .create()
            .show()

        resetValues()
        setupQuestion()
    }

    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }

    private fun resetValues() {
        totalCorrect = 0.0
        index = 0
    }

    private fun setupQuestion() {
        getQuestion(index)

        correctAnswer = question!!.correctAnswer
//        quizQuestionNumber.text = (index + 1).toString()
        question_text_view.text = Html.fromHtml(question!!.question, 0)
    }

    private fun getQuestion(index: Int) {
//        if (swipeRefresh.isRefreshing) {
//            swipeRefresh.isRefreshing = false
//            question = questions[index]
//        } else {
//            question = questions[index]
//        }
        question = questions[index]
    }

    private fun getQuestions() {
        val fetchQuestionsList = apiService.getQuestions()

        fetchQuestionsList.enqueue(
            object : Callback<QuestionList> {
                override fun onResponse(
                    call: Call<QuestionList>,
                    response: Response<QuestionList>
                ) {
                    if (response.isSuccessful) {
                        onFetchQuestionsSuccess(response.body())
                    }
                }

                override fun onFailure(call: Call<QuestionList>, t: Throwable) {
                    Toast.makeText(applicationContext, "Error, item not found", Toast.LENGTH_LONG)
                        .show()
                }
            }
        )
    }

    private fun onFetchQuestionsSuccess(questionList: QuestionList?) {
        questionList?.results?.forEach {
            questions.add(it)
        }
        setupQuestion()
    }

    private fun showRefreshDialog() {
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.setTitle("Are you sure?")
            .setMessage("Are you sure you would like to reload for new questions?")
            .setPositiveButton("Confirm") { _, _ ->
//                swipeRefresh.isRefreshing = true
                resetValues()
                getQuestions()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
//                swipeRefresh.isRefreshing = false
            }
            .create()
            .show()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val NUMBER_OF_QUESTIONS = 10
        private val apiClient = RestAPIClient(getURL())
        private lateinit var apiService: ApiService
        private var instance = App()

        fun getInstance(): App {
            return instance
        }

        fun getURL(): String {
            return "https://opentdb.com"
        }

        fun getApiClient(): RestAPIClient {
            return apiClient
        }

        fun getApiService(): ApiService {
            return apiService
        }
    }
}
