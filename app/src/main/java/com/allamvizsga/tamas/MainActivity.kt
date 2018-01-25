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
                        title = "Kolozsvari seta 3",
                        description = "Kicsi seta kicsi seta 3",
                        imageUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699",
                        stations = arrayListOf(
                            Station(
                                title = "Allomas",
                                coordinate = Coordinate(42.56, -38.98),
                                description = "Allomas leiras",
                                imageUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699"
                            ),
                            Station(
                                title = "Allomas2",
                                coordinate = Coordinate(42.56, -38.98),
                                description = "Allomas leiras",
                                imageUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699"
                            ),
                            Station(
                                title = "Allomas 3",
                                coordinate = Coordinate(42.56, -38.98),
                                description = "Allomas leiras",
                                imageUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699"
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
