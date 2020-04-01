package com.example.geoquiz

import com.example.geoquiz.api.model.ApiQuestion
import com.example.geoquiz.api.model.makeToast
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec

class CheckToastText: DescribeSpec() {

    private val trueQuestion = ApiQuestion("Vehicles", "boolean", "easy", "Do manuals have a clutch?", true, listOf(false))

    init {
        describe("Test that the make toast function is working") {
            context("user is correct"){
                it("should return correct_toast"){
                    trueQuestion.makeToast(true) shouldBe R.string.correct_toast
                }
            }

            context("user is incorrect"){
                it("should return incorrect_toast"){
                    trueQuestion.makeToast(false) shouldBe R.string.incorrect_toast
                }
            }
        }
    }
}