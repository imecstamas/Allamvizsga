package com.allamvizsga.tamas

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.allamvizsga.tamas.model.Coordinate
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.model.Walk
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        upload_button.setOnClickListener {
            //TODO add Repository with rxjava (convert database change events into rx)

            uploadWalk(Walk(title = "Kolozsvari seta 3", description = "Kicsi seta kicsi seta 3", imageUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699", stations = arrayListOf(Station("Allomas", Coordinate(42.56, -38.98), "Allomas leiras", "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699"), Station("Allomas2", Coordinate(42.56, -38.98), "Allomas leiras", "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699"), Station("Allomas 3", Coordinate(42.56, -38.98), "Allomas leiras", "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699"))))
        }

        download_button.setOnClickListener {
            WalkRepository().getAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ walks ->
                        println("Walks: $walks")
                    }, { error ->
                        error.printStackTrace()
                    })
        }
    }
}
