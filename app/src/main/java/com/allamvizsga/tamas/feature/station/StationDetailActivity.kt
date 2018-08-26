package com.allamvizsga.tamas.feature.station

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.StationDetailBinding
import com.allamvizsga.tamas.feature.quiz.QuizActivity
import com.allamvizsga.tamas.feature.walk.list.WalkListActivity
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.util.extension.setUpToolbar
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import org.koin.android.viewmodel.ext.android.getViewModel

class StationDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<StationDetailBinding>(this, R.layout.station_detail_activity).apply {
            viewModel = getViewModel<StationViewModel>().also { viewModel ->
                intent.getParcelableExtra<Station>(STATION).let { station ->
                    if (station != null) {
                        viewModel.initStation(station)
                    } else {
                        viewModel.getStationById(intent.getStringExtra(STATION_ID))
                    }
                }
                nextStation.setOnClickListener {
                    viewModel.nextStation.value.let { nextStation ->
                        if (nextStation != null) {
                            startActivity(QuizActivity.getStartIntent(this@StationDetailActivity, nextStation))
                        } else {
                            //Unregister the geofence for the previous station
                            Awareness.getFenceClient(this@StationDetailActivity).updateFences(FenceUpdateRequest.Builder().removeFence(viewModel.getRegisteredStationId()).build())
                            viewModel.stopWalk()
                            startActivity(WalkListActivity.getStartIntent(this@StationDetailActivity))
                        }
                    }
                }
            }
            setUpToolbar(toolbar)
        }
    }

    companion object {

        private const val STATION_ID = "station_id"
        private const val STATION = "station"

        fun getStartIntent(context: Context, station: Station? = null, stationId: String? = null): Intent =
                Intent(context, StationDetailActivity::class.java).putExtra(STATION_ID, stationId).putExtra(STATION, station)
    }
}
