package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import com.bignerdranch.android.geoquiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private var binding: ActivityMainBinding? = null
    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }
    private val requestCodeCheat = 0

    companion object {
        const val KEY_INDEX = "index"
        const val KEY_SCORE = "score"
        const val KEY_ANSWERSCOUNTER = "answersCounter"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewBinding()
        getSavedInstanceState(savedInstanceState)
        setupClickListener()
        setButtonsStatus(quizViewModel.answers[quizViewModel.currentIndex]!!)
        updateQuestion()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == requestCodeCheat) {
            quizViewModel.isCheater =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        savedInstanceState.putInt(KEY_SCORE, quizViewModel.score)
        savedInstanceState.putInt(KEY_ANSWERSCOUNTER, quizViewModel.answersCounter)
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
        quizViewModel.currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.score = savedInstanceState?.getInt(KEY_SCORE, 0) ?: 0
        quizViewModel.answersCounter = savedInstanceState?.getInt(KEY_ANSWERSCOUNTER, 0) ?: 0
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
            setButtonsStatus(quizViewModel.answers[quizViewModel.currentIndex]!!)
            updateQuestion()
        }
        binding?.prevButton?.setOnClickListener {
            moveToPrev()
            setButtonsStatus(quizViewModel.answers[quizViewModel.currentIndex]!!)
            updateQuestion()
        }
        binding?.questionTextView?.setOnClickListener { view: View ->
            moveToNext()
            setButtonsStatus(quizViewModel.answers[quizViewModel.currentIndex]!!)
            updateQuestion()
        }
        binding?.cheatButton?.setOnClickListener {
            val answerIsTrue = QuestionsBank.getQuestionAnswer(quizViewModel.currentIndex)
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            startActivityForResult(intent, requestCodeCheat)
        }
    }

    private fun updateQuestion() {
        val questionTextResId = QuestionsBank.getQuestionText(quizViewModel.currentIndex)
        binding?.questionTextView?.setText(questionTextResId)
    }

    private fun setAnswer() {
        quizViewModel.answers += quizViewModel.currentIndex to true
        quizViewModel.answersCounter++
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = QuestionsBank.getQuestionAnswer(quizViewModel.currentIndex)
        val messageResId: String
        when {
            quizViewModel.isCheater -> {
                messageResId = getString(R.string.judgment_toast)
            }
            userAnswer == correctAnswer -> {
                messageResId = getString(R.string.correct_toast)
                quizViewModel.score++
            }
            else -> {
                messageResId = getString(R.string.incorrect_toast)
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        quizViewModel.isCheater = false
        setButtonsStatus(false)
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

    private fun setButtonsStatus(status: Boolean) {
        binding?.trueButton?.isEnabled = status
        binding?.falseButton?.isEnabled = status
    }
}
