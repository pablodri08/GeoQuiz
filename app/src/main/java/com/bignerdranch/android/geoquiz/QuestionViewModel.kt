package com.bignerdranch.android.geoquiz

import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {

    var currentIndex = 0
    var score = 0
    var answersCounter = 0
    var answers = Array<Boolean>(QuestionsBank.getBankSize()) { false }
}