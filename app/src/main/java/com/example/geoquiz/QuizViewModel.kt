package com.example.geoquiz

import android.text.Html
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.geoquiz.api.ApiService
import com.example.geoquiz.api.model.ApiQuestion
import com.example.geoquiz.api.model.QuestionList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuizViewModel(val apiService: ApiService) : ViewModel() {

    val viewState: MutableLiveData<QuizViewState> = MutableLiveData()

    init {
        viewState.value = QuizViewState(
            progressType = ProgressType.NotAsked,
            correctAnswer = true,
            userAnswer = false,
            questions = mutableListOf(),
            question = ApiQuestion(),
            index = 0,
            totalCorrect = 0,
            isCheater = false

        )
    }

    fun currentViewState(): QuizViewState = viewState.value!!

    fun checkAnswer(userAnswer: Boolean, correctAnswer: Boolean): Int {
        if(currentViewState().isCheater){
            return R.string.judgment_toast
        } else {
            if (userAnswer == correctAnswer) {
                updateState(
                    currentViewState().copy(
                        totalCorrect = currentViewState().totalCorrect + 1,
                        progressType = ProgressType.Result
                    )
                )
                return R.string.correct_toast
            } else {
                return R.string.incorrect_toast
            }
        }
    }
    fun userCheat(){
        updateState(
            currentViewState().copy(
                isCheater = true
            )
        )
    }

    fun getQuestions() {
        val fetchQuestionsList = apiService.getQuestions()
        updateState(
            currentViewState().copy(
                progressType = ProgressType.Loading
            )
        )
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
                    updateState(
                        currentViewState().copy(
                            progressType = ProgressType.Error
                        )
                    )
                }
            }
        )
    }

    fun onFetchQuestionsSuccess(questionList: QuestionList?) {
        var questions: MutableList<ApiQuestion> = mutableListOf()

        questionList?.results?.forEach {
            questions.add(it)
        }

        updateState(
            currentViewState().copy(
                progressType = ProgressType.Result,
                questions = questions
            )
        )
        setupQuestion()
    }

    fun updateIndex(i: Int) {
        updateState(
            currentViewState().copy(
                index = i
            )
        )
    }

    fun updateState(index: Int, totalCorrect: Int, questions: MutableList<ApiQuestion>){
        updateState(
            currentViewState().copy(
                index = index,
                totalCorrect = totalCorrect
            )
        )
    }

    fun incIndex() {
        updateState(
            currentViewState().copy(
                index = currentViewState().index + 1,
                progressType = ProgressType.Result
            )
        )
    }

    fun decIndex() {
        updateState(
            currentViewState().copy(
                index = currentViewState().index - 1
            )
        )
    }

    fun setupQuestion() {
        val question = getQuestion(currentViewState().index)
        updateState(
            currentViewState().copy(
                question = question,
                correctAnswer = question.correctAnswer,
//                isCheater = false,
                progressType = ProgressType.NotAsked
            )
        )
    }

    private fun getQuestion(index: Int) = currentViewState().questions[index]

    private fun updateState(newState: QuizViewState) {
        viewState.value = currentViewState().copy(
            progressType = newState.progressType,
            correctAnswer = newState.correctAnswer,
            userAnswer = newState.userAnswer,
            question = newState.question,
            questions = newState.questions,
            totalCorrect = newState.totalCorrect,
            index = newState.index
        )
    }

    fun resetProgress(){
        viewState.value = currentViewState().copy(
            progressType = ProgressType.NotAsked,
            totalCorrect = 0,
            userAnswer = false,
            index = 0,
            isCheater = false
        )
    }

    data class QuizViewState(
        val progressType: ProgressType,
        val correctAnswer: Boolean,
        val userAnswer: Boolean,
        val questions: MutableList<ApiQuestion>,
        val question: ApiQuestion,
        val index: Int,
        val totalCorrect: Int,
        val isCheater: Boolean
    )

}