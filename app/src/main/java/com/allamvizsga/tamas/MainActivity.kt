package com.allamvizsga.tamas

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.allamvizsga.tamas.model.Coordinate
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.model.Walk
import com.allamvizsga.tamas.storage.repository.WalkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val walkRepository: WalkRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        upload_button.setOnClickListener {
            walkRepository.saveWalk(Walk(title = "Kolozsvari seta 3",
                    description = "Kicsi seta kicsi seta 3",
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699",
                    stations = arrayListOf(
                            Station(title = "Allomas", coordinate = Coordinate(42.56, -38.98), description = "Allomas leiras", imageUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699"),
                            Station(title = "Allomas2", coordinate = Coordinate(42.56, -38.98), description = "Allomas leiras", imageUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699"),
                            Station(title = "Allomas 3", coordinate = Coordinate(42.56, -38.98), description = "Allomas leiras", imageUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699")
                    )))
        }

        var id = ""
        download_all_button.setOnClickListener {
            walkRepository.getAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ walks ->
                        println("Walks: $walks")
                        id = walks[0].id!!
                    }, { error ->
                        error.printStackTrace()
                    })
        }

        download_one_button.setOnClickListener {
            walkRepository.getById(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ walk ->
                        println("Walk: $walk")
                    }, { error ->
                        error.printStackTrace()
                    })
        }
    }
}
