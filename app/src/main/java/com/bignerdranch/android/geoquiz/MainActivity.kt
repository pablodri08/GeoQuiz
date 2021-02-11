package com.bignerdranch.android.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import com.bignerdranch.android.geoquiz.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val keyIndex = "index"
    private val keyScore = "score"
    private val keyAnswersCounter = "answersCounters"
    private var binding: ActivityMainBinding? = null
    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewBinding()
        getSavedInstanceState(savedInstanceState)
        setupClickListener()
        setupAnswerButtonState()
        updateQuestion()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(keyIndex, quizViewModel.currentIndex)
        savedInstanceState.putInt(keyScore, quizViewModel.score)
        savedInstanceState.putInt(keyAnswersCounter, quizViewModel.answersCounter)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setupViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)
    }

    private fun getSavedInstanceState(savedInstanceState: Bundle?) {
        val currentIndex = savedInstanceState?.getInt(keyIndex, 0) ?: 0
        quizViewModel.currentIndex = currentIndex
        val score = savedInstanceState?.getInt(keyScore, 0) ?: 0
        quizViewModel.score = score
        val totalAnswers = savedInstanceState?.getInt(keyAnswersCounter, 0) ?: 0
        quizViewModel.answersCounter = totalAnswers
    }

    private fun setupClickListener() {
        binding?.trueButton?.setOnClickListener { view: View ->
            setAnswer()
            checkAnswer(true)
            checkResult()
        }
        binding?.falseButton?.setOnClickListener { view: View ->
            setAnswer()
            checkAnswer(false)
            checkResult()
        }
        binding?.nextButton?.setOnClickListener {
            moveToNext()
            setupAnswerButtonState()
            updateQuestion()
        }
        binding?.prevButton?.setOnClickListener {
            moveToPrev()
            setupAnswerButtonState()
            updateQuestion()
        }
        binding?.questionTextView?.setOnClickListener { view: View ->
            moveToNext()
            setupAnswerButtonState()
            updateQuestion()
        }
    }

    private fun setupAnswerButtonState() {
        if (quizViewModel.answers[quizViewModel.currentIndex]) {
            disableButtons()
        } else {
            enableButtons()
        }
    }

    private fun updateQuestion() {
        val questionTextResId = QuestionsBank.getQuestionText(quizViewModel.currentIndex)
        binding?.questionTextView?.setText(questionTextResId)
    }

    private fun setAnswer() {
        quizViewModel.answers[quizViewModel.currentIndex] = true
        quizViewModel.answersCounter++
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = QuestionsBank.getQuestionAnswer(quizViewModel.currentIndex)
        val messageResId: String
        if (userAnswer == correctAnswer) {
            quizViewModel.score++
            messageResId = getString(R.string.correct_toast)
        } else {
            messageResId = getString(R.string.incorrect_toast)
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        disableButtons()
    }

    private fun checkResult() {
        if (quizViewModel.answersCounter == QuestionsBank.getBankSize()) {
            val result = ((quizViewModel.score.toDouble() / quizViewModel.answersCounter) * 100)
            var resultMessage = getString(R.string.quizResult).plus(result)
            Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveToPrev() {
        quizViewModel.currentIndex =
            if (quizViewModel.currentIndex - 1 < 0) {
                QuestionsBank.getBankSize() - 1
            } else {
                quizViewModel.currentIndex - 1
            }
    }

    private fun moveToNext() {
        quizViewModel.currentIndex = (quizViewModel.currentIndex + 1) % QuestionsBank.getBankSize()
    }

    private fun disableButtons() {
        binding?.trueButton?.isEnabled = false
        binding?.falseButton?.isEnabled = false
    }

    private fun enableButtons() {
        binding?.trueButton?.isEnabled = true
        binding?.falseButton?.isEnabled = true
    }
}

