package com.allamvizsga.tamas.feature.quiz

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.allamvizsga.tamas.QuizBinding
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.feature.response.ResponseActivity
import com.allamvizsga.tamas.feature.shared.BaseActivity
import com.allamvizsga.tamas.model.Answer
import com.allamvizsga.tamas.model.Question
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.util.extension.setUpToolbar
import org.koin.android.architecture.ext.getViewModel
import org.koin.android.ext.android.setProperty

class QuizActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        TODO add the question into the station
        val question = Question("A kolozsvári Képzőművészeti és Formatervezési Egyetem eredetileg __________ volt. ", listOf(Answer("katonai állomás", false), Answer("Mátyás király szülőháza", true), Answer("bevásárlóközpont", false)))

        DataBindingUtil.setContentView<QuizBinding>(this, R.layout.quiz_activity).apply {
            setProperty(QUESTION, question)
            viewModel = getViewModel<QuizViewModel>().also {
                checkButton.setOnClickListener { view ->
                    ActivityCompat.startActivity(this@QuizActivity, ResponseActivity.getStartIntent(this@QuizActivity, view, it.isAnswerCorrect(), intent.getParcelableExtra(NEXT_STATION)), null)
                    overridePendingTransition(0, 0)
                }
            }
            setUpToolbar(toolbar)
        }
    }

    companion object {

        const val QUESTION = "quiz"
        private const val NEXT_STATION = "next_station"

        fun getStartIntent(context: Context, nextStation: Station): Intent =
                Intent(context, QuizActivity::class.java)
                        .putExtra(NEXT_STATION, nextStation)
    }
}
