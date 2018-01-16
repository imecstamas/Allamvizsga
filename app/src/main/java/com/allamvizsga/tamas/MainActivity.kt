package com.allamvizsga.tamas

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.allamvizsga.tamas.model.Coordinate
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.model.Walk
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            //TODO add Repository with rxjava (convert database change events into rx)

            uploadWalk(Walk("Kolozsvari seta 2", "Kicsi seta kicsi seta 2", "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699", arrayListOf(Station("Allomas", Coordinate(42.56, -38.98), "Allomas leiras", "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699"), Station("Allomas2", Coordinate(42.56, -38.98), "Allomas leiras", "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699"), Station("Allomas 3", Coordinate(42.56, -38.98), "Allomas leiras", "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699"))))
//            database.child("stations").push().setValue(Station("Allomas", Coordinate(42.56, -38.98), "Allomas leiras", "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699"))
        }
    }
}
