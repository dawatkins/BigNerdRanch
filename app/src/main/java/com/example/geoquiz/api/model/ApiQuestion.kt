package com.example.geoquiz.api.model

import android.os.Parcelable
import com.example.geoquiz.Question
import com.example.geoquiz.R
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ApiQuestion(
    val category: String? = "",
    val type: String? = "",
    val difficulty: String? = "",
    val question: String? = "",
    @SerializedName("correct_answer") val correctAnswer: Boolean = true,
    @SerializedName("incorrect_answer") val incorrectAnswer: List<Boolean> = listOf(false)
) : Parcelable

fun ApiQuestion.checkAnswer(userAnswer: Boolean): Boolean = userAnswer == correctAnswer

fun ApiQuestion.makeToast(userAnswer: Boolean): Int{
    if(userAnswer)
        return R.string.correct_toast
    else
        return R.string.incorrect_toast

}