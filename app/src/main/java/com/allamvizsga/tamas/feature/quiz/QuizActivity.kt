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
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.util.extension.setUpToolbar
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class QuizActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<QuizBinding>(this, R.layout.quiz_activity).apply {
            viewModel = getViewModel<QuizViewModel> { parametersOf(intent.getParcelableExtra<Station>(NEXT_STATION).question) }.also {
                checkButton.setOnClickListener { view ->
                    ActivityCompat.startActivity(this@QuizActivity, ResponseActivity.getStartIntent(this@QuizActivity, view, it.isAnswerCorrect(), intent.getParcelableExtra(NEXT_STATION)), null)
                    overridePendingTransition(0, 0)
                }
            }
            setUpToolbar(toolbar)
        }
    }

    companion object {

        private const val NEXT_STATION = "next_station"

        fun getStartIntent(context: Context, nextStation: Station): Intent =
                Intent(context, QuizActivity::class.java)
                        .putExtra(NEXT_STATION, nextStation)
    }
}
