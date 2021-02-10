package com.bignerdranch.android.geoquiz

object QuestionsBank {
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    fun getBankSize() = questionBank.size

    fun getQuestionText(index: Int) = questionBank[index].textResId

    fun getQuestionAnswer(index: Int) = questionBank[index].answer
}
