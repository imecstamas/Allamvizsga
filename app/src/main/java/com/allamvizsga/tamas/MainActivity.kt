package com.allamvizsga.tamas

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.allamvizsga.tamas.databinding.ActivityMainBinding
import com.allamvizsga.tamas.feature.walk.list.WalkListActivity
import com.allamvizsga.tamas.model.Coordinate
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.model.Walk
import com.allamvizsga.tamas.storage.repository.WalkRepository
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val walkRepository: WalkRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            uploadButton.setOnClickListener {
                walkRepository.saveWalk(
                        Walk(
                                title = "Fontos helyek",
                                description = "Fontos helyek mint munkahely es otthon",
                                imageUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699",
                                stations = arrayListOf(
                                        Station(
                                                title = "Otthon",
                                                coordinate = Coordinate(46.763212, 23.597362),
                                                description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                                                imageUrl = "http://static.panoramio.com/photos/large/42441030.jpg"
                                        ),
                                        Station(
                                                title = "Munkahely",
                                                coordinate = Coordinate(46.78848085, 23.59455932),
                                                description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                                                imageUrl = "http://clujbusiness.ro/wp-content/uploads/2016/06/halcyon_mobile_a-01.png"
                                        )
                                )
                        )
                )
            }

            walksButton.setOnClickListener {
                startActivity(Intent(this@MainActivity, WalkListActivity::class.java))
            }
        }
    }
}
