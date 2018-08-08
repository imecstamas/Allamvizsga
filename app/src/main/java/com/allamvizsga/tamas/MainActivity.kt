package com.allamvizsga.tamas

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.allamvizsga.tamas.databinding.ActivityMainBinding
import com.allamvizsga.tamas.model.*
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
                                title = "Mátyás király",
                                description = "I. Mátyás – gyakran Corvin Mátyás, születési nevén Hunyadi Mátyás, hivatalos latin uralkodói nevén Mathias Rex; neve németül, latinul, angolul: Matthias Corvinus, olaszul: Mattia Corvino, románul: Matei Corvin, csehül: Matyáš Korvín, horvátul Matija Korvin – (Kolozsvár, 1443. február 23. – Bécs, 1490. április 6.) Magyarország királya 1458 és 1490 között. Hivatalos uralkodói címe eredetileg Magyarország, Dalmácia, Horvátország, Ráma, Szerbia, Galícia, Lodoméria, Kunország és Bulgária királya volt, ehhez csatlakozott 1469 után, cseh királlyá választásától a Csehország királya cím is. Corvinus, azaz Hollós melléknevét a Hunyadi-család hollót ábrázoló címeréről kapta.",
                                imageUrl = "https://hu.wikipedia.org/wiki/I._M%C3%A1ty%C3%A1s_magyar_kir%C3%A1ly#/media/File:Matthias_Corvinus_from_a_Corvina_Codex.jpg",
                                stations = arrayListOf(
                                        Station(
                                                title = "Mátyás szülőháza",
                                                coordinate = Coordinate(46.771167, 23.587532),
                                                description = "A ház az 1440-es években Méhffi Jakab jómódú szőlősgazda tulajdonában állt, rendszerint nála szállt meg Szilágyi Erzsébet. 1443. február 23-án (szülőházi emléktáblájának felirata szerint március 27-én) született Hunyadi Mátyás. 1467. szeptember 28-án Mátyás király minden adó alól felmentette a ház akkori tulajdonosát, Kolb Istvánt, Méhffi Jakab vejét, valamint feleségét, Orsolya asszonyt, és annak testvérét, Margitot, és minden leszármazottjukat a ház és a hozzátartozó birtokok adója alól. A kiváltság, amelyet 1649-ben II. Rákóczi György is megerősített, rendkívül értékessé tette az épületet, és biztosította aránylag változatlan fennmaradását. Az épület keleti szárnyát a 16. században építették, a két szárnyat összekötő átjáró szemöldökkövén az 1578-as évszám található.",
                                                imageUrl = "https://archivum.ujszo.com/sites/default/files/photos/promoted/kmatyasfoto.jpg",
                                                audioUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/audio%2FVelence%20%20varosnezes.mp3?alt=media&token=acba7a67-dcb7-43dc-bad5-34329d6aa14f",
                                                question = Question("A kolozsvári Képzőművészeti és Formatervezési Egyetem eredetileg __________ volt. ", listOf(Answer("katonai állomás", false), Answer("Mátyás király szülőháza", true), Answer("bevásárlóközpont", false)))
                                        ),
                                        Station(
                                                title = "Mátyás szobor",
                                                coordinate = Coordinate(46.769709, 23.589887),
                                                description = "Kolozsvár városának régóta dédelgetett terve volt, hogy nagy szülöttének, Mátyás királynak maradandó emléket állítson. Az emlékmű elkészítése érdekében a város 1882 májusában szoborbizottságot alakított és a nagy terv mögé rövidesen az ország egész közvéleménye felsorakozott. A terv a főtér reprezentatív igényű átalakításával párhuzamosan született meg, a szobor helye maga is a Szent Mihály-templom körüli elárusító bódék elbontásával szabadult föl. Kolozsvár város tanácsa 5000 forintot szavazott meg a szoboralap javára, a maradék összeget pedig közadakozásból szándékozott előteremteni. Az országos gyűjtés eredményeképpen 1892 júliusára a szoboralap tőkéje már meghaladta a 120 000 koronát, amely lehetővé tette a pályázat kiírását.\n" +
                                                        "\n" +
                                                        "A sok pályázó közül végül hármat, Fadrusz János, Bezerédi Gyula és Róna József munkáit találták a legjobbnak, az első díjat pedig – a kivitelezési megbízással együtt – Fadrusz Jánosnak ítélték. A megkötött szerződés szerint a szobor költségeit 200 000 koronában határozták meg. A művész azonban figyelembe véve a szobor elhelyezését arányszámításai alapján kevésnek találta a másfélszeres életnagyságú szobrokat és a kétszeres életnagyságra tett javaslatot. Az új javaslat a költségeket még 60 000 koronával emelte meg.",
                                                imageUrl = "https://www.vitato.eu/wp-content/uploads/kolozsvari-matyas-szobor.jpg",
                                                audioUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/audio%2FVelence%20%20varosnezes.mp3?alt=media&token=acba7a67-dcb7-43dc-bad5-34329d6aa14f",
                                                question = Question("Melyik műemléket alkotta Fadrusz János?", listOf(Answer("Szent György-szobor", false), Answer("Mátyás szobor", true), Answer("Márton Áron szobor", false)))
                                        )
                                )
                        )
                )
            }
        }
    }
}
