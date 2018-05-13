package com.allamvizsga.tamas

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.allamvizsga.tamas.databinding.ActivityMainBinding
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
                            title = "Kolozsvar a kommunizmusban",
                            description = "Nicolae Ceaușescu kommunista diktátor szó szerint az éj leple alatt döntötte el, hogy Kolozsvár felvegye a Napoca nevet is – írja a ziadecj.ro. A portál abból az alkalomból közölt szombaton erről összeállítást, hogy október 16-án lesz a kincses város átkeresztelésének negyvenedik évfordulója.\n" +
                                    "\n" +
                                    "1974. október 15-ig Erdély történelmi fővárosát Kolozsvárnak (románul Cluj) hívták. Másnap azonban az Államtanács dekrétumban elrendelte, hogy a megyeszékhely nevéhez toldják hozzá a Napocát is. Így lett a város neve hivatalosan románul Cluj-Napoca. Ugyanazon a napon rendelték el Turnu Severin átkeresztelését is Drobeta-Turnu Severinre.",
                            imageUrl = "http://www.maszol.ro/uploads/files/userfiles/images/belfold/2013/augusztus/10/kvar.jpg",
                                stations = arrayListOf(
                                        Station(
                                            title = "Megyehaza",
                                                coordinate = Coordinate(46.7632567, 23.597373100000027),
                                            description = "A kolozsvári megyeháza (prefektúra) a város egyik látványos, szecessziós stílusú kétemeletes épülete, a Belső-Magyar (Kossuth, Mareșal Foch, B-dul 21 Decembrie 1989) utca és a Bocskai (Avram Iancu) tér sarkán áll. A romániai műemlékek jegyzékében a CJ-II-m-B-07267 sorszámon szerepel.\n" +
                                                    "\n" +
                                                    "Az épület a gótikus‚ reneszánsz és mór stílus elemeinek és virágmotívumoknak a kombinációja. A nagy alapterületű épületnek két belső udvara van. Huber József tervei alapján 1910-ben épült fel, eredetileg a Kereskedelmi és Iparkamara számára. Helyén korábban a Magyar Korona vendégfogadó állt.\n" +
                                                    "\n" +
                                                    "A kolozsvári Kereskedelmi és Iparkamara 1851. január 19-én alakult meg, és képviselte a kereskedők és iparosok érdekeit. Kiállt a vasút bevezetéséért‚ a vámrendszer egyszerűsítéséért‚ a szakképzés fejlesztéséért; lapokat jelentetett meg. Első elnöke Dietrich Sámuel vaskereskedő‚ alelnöke pedig Rajka Péter gyáros volt.\n" +
                                                    "\n" +
                                                    "Az Ellenzék című napilap kiadóhivatala és nyomdája egy ideig több helyiséget bérelt itt. 1914 körül itt volt Kohn Lajos fűszerkereskedési cégének a központja. 1911-től 1919-ig az egyik lakosztályt Schmidt Henrik‚ az egyetem német nyelv és irodalom tanszékének új tanára bérelte. Az első világháború után szintén kereskedelmi és iparkamara volt a rendeltetése, de bérelt helyiségeket a VI. Hadosztály parancsnoksága‚ az Elite sportklub‚ a CFR-nyomda is. 1926-tól a Societatea de Mâine (A holnap társadalma) című társadalmi és gazdasági hetilap szerkesztősége és kiadóhivatala is itt volt. Az 1948-as államosításkor a kamara megszűnt, az épületben ezek után városháza, majd a kommunista párt tartományi, illetve megyei bizottságának székháza volt. A megyei tanács és a megyefőnöki (prefektusi) hivatal 1991-92-ben költözött ide.",
                                            imageUrl = "https://hu.wikipedia.org/wiki/Megyeh%C3%A1za_(Kolozsv%C3%A1r)#/media/File:Kolozsvar_megyehaza2.JPG",
                                                audioUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/audio%2FVelence%20%20varosnezes.mp3?alt=media&token=acba7a67-dcb7-43dc-bad5-34329d6aa14f"
                                        )
//                                    ,
//                                        Station(
//                                                title = "Matyas szobra",
//                                                coordinate = Coordinate(46.78848085, 23.59455932),
//                                                description = "Kolozsvár városának régóta dédelgetett terve volt, hogy nagy szülöttének, Mátyás királynak maradandó emléket állítson. Az emlékmű elkészítése érdekében a város 1882 májusában szoborbizottságot alakított és a nagy terv mögé rövidesen az ország egész közvéleménye felsorakozott. A terv a főtér reprezentatív igényű átalakításával párhuzamosan született meg, a szobor helye maga is a Szent Mihály-templom körüli elárusító bódék elbontásával szabadult föl. Kolozsvár város tanácsa 5000 forintot szavazott meg a szoboralap javára, a maradék összeget pedig közadakozásból szándékozott előteremteni. Az országos gyűjtés eredményeképpen 1892 júliusára a szoboralap tőkéje már meghaladta a 120 000 koronát, amely lehetővé tette a pályázat kiírását.\n" +
//                                                        "\n" +
//                                                        "A sok pályázó közül végül hármat, Fadrusz János, Bezerédi Gyula és Róna József munkáit találták a legjobbnak, az első díjat pedig – a kivitelezési megbízással együtt – Fadrusz Jánosnak ítélték. A megkötött szerződés szerint a szobor költségeit 200 000 koronában határozták meg. A művész azonban figyelembe véve a szobor elhelyezését arányszámításai alapján kevésnek találta a másfélszeres életnagyságú szobrokat és a kétszeres életnagyságra tett javaslatot. Az új javaslat a költségeket még 60 000 koronával emelte meg.",
//                                                imageUrl = "https://hu.wikipedia.org/wiki/M%C3%A1ty%C3%A1s_kir%C3%A1ly_eml%C3%A9km%C5%B1#/media/File:Matyas_kiraly_emlekmu_Kolozsvar.jpg",
//                                                audioUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/audio%2FVelence%20%20varosnezes.mp3?alt=media&token=acba7a67-dcb7-43dc-bad5-34329d6aa14f"
//                                        )
                                )
                        )
                )
            }
        }
    }
}
