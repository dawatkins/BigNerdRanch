package com.example.geoquiz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.geoquiz.api.ApiService
import com.example.geoquiz.api.RestAPIClient
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0
private const val NUMBER_OF_QUESTIONS = 10

class MainActivity : AppCompatActivity() {

    lateinit var quizViewModel: QuizViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        apiService = apiClient.getApiService()
        quizViewModel = QuizViewModel(apiService)

        Log.d(TAG, "onCreate(Bundle?) called")

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.updateIndex(currentIndex)


        quizViewModel.viewState.observe(this, Observer<QuizViewModel.QuizViewState> {
            render(quizViewModel.currentViewState())
        })

        true_button.setOnClickListener {
            val messageResId = quizViewModel.checkAnswer(true, quizViewModel.currentViewState().correctAnswer)
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
            true_button.isClickable = false
            false_button.isClickable = false

        }

        false_button.setOnClickListener {
            val messageResId = quizViewModel.checkAnswer(false, quizViewModel.currentViewState().correctAnswer)
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
            false_button.isClickable = false
            true_button.isClickable = false
        }

        next_button.setOnClickListener {
            if (quizViewModel.currentViewState().index < quizViewModel.currentViewState().questions.size - 1) {
                quizViewModel.incIndex()
            } else {
                displayGrade()
                quizViewModel.resetProgress()
            }
            false_button.isClickable = true
            true_button.isClickable = true
        }

        previous_button.setOnClickListener {
            if (quizViewModel.currentViewState().index > 0) {
                quizViewModel.decIndex()
                quizViewModel.setupQuestion()
                renderResult()
            } else {
                Toast.makeText(applicationContext, "First question in the list.", Toast.LENGTH_LONG)
                    .show()
            }
            false_button.isClickable = false
            true_button.isClickable = false
        }


        cheat_button.setOnClickListener {
            val answer = quizViewModel.currentViewState().correctAnswer
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

//        if (requestCode == REQUEST_CODE_CHEAT) {
//            quizViewModel.isCheater =
//                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
//        }
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
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentViewState().index)
        savedInstanceState.putInt("CORRECT", quizViewModel.currentViewState().totalCorrect)
        savedInstanceState.putParcelableArrayList("QUESTIONS", ArrayList<Parcelable>(quizViewModel.currentViewState().questions))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            quizViewModel.updateState(savedInstanceState.getInt("INDEX"), savedInstanceState.getInt("CORRECT"), savedInstanceState.getParcelableArrayList("QUESTIONS"))
//            quizViewModel.setupQuestion()
        } else {
            quizViewModel.getQuestions()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun displayGrade() {
        val totalCorrect = quizViewModel.currentViewState().totalCorrect
        val questionsSize = quizViewModel.currentViewState().questions.size
        val gradePercent = ((totalCorrect * 100) / questionsSize)
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Quiz Grade")
            .setMessage("You got $totalCorrect correct out of $questionsSize.\nYour grade was: $gradePercent%")
            .create()
            .show()

        quizViewModel.resetProgress()
        renderResult()
    }

    private fun showRefreshDialog() {
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.setTitle("Are you sure?")
            .setMessage("Are you sure you would like to reload for new questions?")
            .setPositiveButton("Confirm") { _, _ ->
//                swipeRefresh.isRefreshing = true
                quizViewModel.getQuestions()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
//                swipeRefresh.isRefreshing = false
            }
            .create()
            .show()
    }
    private fun render(viewState: QuizViewModel.QuizViewState) {
        when(viewState.progressType) {
            ProgressType.NotAsked -> {}
            ProgressType.Loading -> {}
            ProgressType.Result -> {renderResult()}
            ProgressType.Error -> {renderError()}
        }
    }

    private fun renderResult() {
        quizViewModel.setupQuestion()
        question_text_view.text = (Html.fromHtml(quizViewModel.currentViewState().question.question, Html.FROM_HTML_MODE_LEGACY))
    }

    private fun renderError(){ Toast.makeText(applicationContext, "Error, item not found", Toast.LENGTH_LONG).show() }


    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val apiClient = RestAPIClient(getURL())
        private lateinit var apiService: ApiService
        private var instance = App()

        fun getInstance(): App {
            return instance
        }

        private fun getURL(): String {
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
