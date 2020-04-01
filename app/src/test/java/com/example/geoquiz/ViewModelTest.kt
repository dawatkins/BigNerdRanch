package com.example.geoquiz

import com.example.geoquiz.api.ApiService
import com.example.geoquiz.api.model.ApiQuestion
import com.example.geoquiz.api.model.QuestionList
import io.kotlintest.specs.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewModelTest : DescribeSpec() {

    private val apiService = mockk<ApiService>()

    init {
        describe("Checks that the Question View Model functions are working"){
            context("Checks that the question data is retrieved from the ApiService") {
                it("Question Data is successfully pulled from the API") {
                    every { apiService.getQuestions() } returns QuestionCall
                }
            }


        }
    }

    object QuestionCall : Call<QuestionList> {
        var failRequest = false

        override fun execute(): Response<QuestionList> {
            val questionList = listOf(
                ApiQuestion("Vehicles", "boolean", "easy", "Do manuals have a clutch?", true, listOf(false)),
                ApiQuestion("General", "boolean", "easy", "Is the grass blue?", false, listOf(true)),
                ApiQuestion("Vehicles", "boolean", "easy", "Fords emblem is a bow tie", false, listOf(true)),
                ApiQuestion("General", "boolean", "easy", "The sky is blue?", true, listOf(false))
            )
            val questionListWrapper = QuestionList(results = questionList)
            return if (failRequest) {
                Response.error(404, ResponseBody.create(null, ""))
            } else {
                Response.success(questionListWrapper)
            }
        }

        override fun enqueue(callback: Callback<QuestionList>) {
            val questionList = listOf(
                ApiQuestion("Vehicles", "boolean", "easy", "Do manuals have a clutch?", true, listOf(false)),
                ApiQuestion("General", "boolean", "easy", "Is the grass blue?", false, listOf(true)),
                ApiQuestion("Vehicles", "boolean", "easy", "Fords emblem is a bow tie", false, listOf(true)),
                ApiQuestion("General", "boolean", "easy", "The sky is blue?", true, listOf(false))
            )
            val questionListWrapper = QuestionList(results = questionList)
            if(failRequest) {
                callback.onResponse(this, Response.error(404, ResponseBody.create(null, "")))
            } else {
                callback.onResponse(this, Response.success(questionListWrapper))
            }
        }

        override fun isExecuted(): Boolean {
            return false
        }
        override fun clone(): Call<QuestionList> {
            return this
        }
        override fun isCanceled(): Boolean {
            return false
        }
        override fun cancel() {
        }
        override fun request(): Request {
            return Request.Builder().build()
        }
    }
}