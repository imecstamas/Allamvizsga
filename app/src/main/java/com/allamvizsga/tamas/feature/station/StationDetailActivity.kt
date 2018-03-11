package com.allamvizsga.tamas.feature.station

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.StationDetailBinding
import org.koin.android.architecture.ext.getViewModel

class StationDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<StationDetailBinding>(this, R.layout.station_detail_activity).apply {
            viewModel = getViewModel<StationViewModel>().also {
                it.getStationById(intent.getStringExtra(STATION_ID))
            }
        }
    }

    companion object {

        private const val STATION_ID = "station_id"

        fun getStartIntent(context: Context, id: String): Intent =
                Intent(context, StationDetailActivity::class.java).putExtra(STATION_ID, id)
    }
}
