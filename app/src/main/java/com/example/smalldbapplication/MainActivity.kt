package com.example.smalldbapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smalldbapplication.model.Star
import com.example.smalldbapplication.ui.theme.SmallDBApplicationTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        //loadTestDataIntoFirestore()
        enableEdgeToEdge()
        setContent {
            SmallDBApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (FirebaseAuth.getInstance().currentUser == null){
                        AppBackground(R.drawable.starry_back_entrace) {
                            AuthScreen(
                                padding = innerPadding,
                                activity = this
                            )
                        }

                    } else {
                        val intent = Intent(this, ListOfDataActivity::class.java)
                        this.startActivity(intent)
                        this.finish()
                    }
                }
            }
        }
    }
    private fun loadTestDataIntoFirestore(){
        val db = Firebase.firestore


        val testData = listOf(
            Star(name = "Sun", type = "Yellow dwarf (G2V)", color = "Yellow-white",
                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR9nW2CphI7G1yiYx77r9_N8alieAs-w_GGDA&s",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQtM0-tgzpGOQZ_hIfeZKnZ4tduur7QNOLxYg&s",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTjYFV-bwRLTx5vbXeIRyRZDH86KNG-4ktGcg&s",
                    "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/e36b69a2-a218-4f43-97c0-dd316a21c699/dfxft43-b4c89c99-9b45-4e61-badd-9861e7050fac.jpg/v1/fill/w_1192,h_670,q_70,strp/sun_by_pickgameru_dfxft43-pre.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9NzIwIiwicGF0aCI6IlwvZlwvZTM2YjY5YTItYTIxOC00ZjQzLTk3YzAtZGQzMTZhMjFjNjk5XC9kZnhmdDQzLWI0Yzg5Yzk5LTliNDUtNGU2MS1iYWRkLTk4NjFlNzA1MGZhYy5qcGciLCJ3aWR0aCI6Ijw9MTI4MCJ9XV0sImF1ZCI6WyJ1cm46c2VydmljZTppbWFnZS5vcGVyYXRpb25zIl19.Gvt_q33whELFdTenhitq4HWltnt-t4pIYtf4oEdesQo",
                    "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/ea6256ec-9ab1-4486-9aed-11f6d376eabd/dgec6jw-5cbd56b4-6ec3-45f5-bdc9-1ce02f3176ca.jpg/v1/fill/w_1131,h_707,q_70,strp/sun_background_by_mendez1996_dgec6jw-pre.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9ODAwIiwicGF0aCI6IlwvZlwvZWE2MjU2ZWMtOWFiMS00NDg2LTlhZWQtMTFmNmQzNzZlYWJkXC9kZ2VjNmp3LTVjYmQ1NmI0LTZlYzMtNDVmNS1iZGM5LTFjZTAyZjMxNzZjYS5qcGciLCJ3aWR0aCI6Ijw9MTI4MCJ9XV0sImF1ZCI6WyJ1cm46c2VydmljZTppbWFnZS5vcGVyYXRpb25zIl19.2wbPR82y7rUEzasS-scXZVnVts7wE8RQehM8jcZNYhI",
                    "https://avatars.mds.yandex.net/get-yapic/38436/enc-2d9c41f88604d3bfd01eb7feb41baf1d2e7106aa3e83a3e6dc61d37e6f0a31ec/orig",
                    "https://img-s-msn-com.akamaized.net/tenant/amp/entityid/AA1v52eE.img?w=1920&h=1304&m=4&q=89"
                    )
            ),
            Star(name = "Sirius (α Canis Majoris)", type = "Main sequence (A1V)", color = "White",
                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTrJculZM4BQlbka6RyDYx5rJRQiBhzD-z1Mw&s",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQk3fDYinVy3mOn14rDEnQe1cS-oekAV0_qjQ&s",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTv-Ph1dJ2863DdAtbfxrOE_1OOT3m_kACQaA&s",
                    "https://2.bp.blogspot.com/-c68up4Vj-xE/UxKzldY1rdI/AAAAAAAAF4s/7N6vQG3A0HU/s1600/18.JPG",
                    "https://spacegid.com/wp-content/uploads/2013/05/Sirius-i-rasseyannoe-skoplenie-M41.jpg",
                    "https://i.pinimg.com/originals/61/9b/02/619b02d8bd3a2e656300144ae6b1631b.jpg",
                    "https://i.pinimg.com/originals/fd/63/55/fd6355080c24ca9a80bc5e490eccc226.jpg"
                )
            ),
            Star(name = "Betelgeuse (α Orionis)", type = "Red supergiant (M1-2Ia-Iab)", color = "Red",
                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQOowJoJ0eHzQMNfiCe2PipKV2rD-ZfNOUDSg&s",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTdYHsz7OSFsWz5-yfJVRoZ0ClpexSgUph7AA&s",
                    "https://freestarcharts.com/images/Articles/Stars/Betelgeuse/Betelgeuse_Hubble.jpg",
                    "https://drujbaspb.ru/wp-content/uploads/d/3/0/d30abf3d132190baee883f0ada718b93.jpeg",
                    "https://svs.gsfc.nasa.gov/vis/a010000/a010600/a010625/Pulsar_Binary_738.jpg",
                    "https://shareslide.ru/img/thumbs/2010b57b21382b51e0dbc4772c2dfd4a-800x.jpg",
                    "https://22century.ru/wp-content/uploads/2020/06/Betelgeuse-58d153423df78c3c4fcbc41c.jpg"
                    )
            ),
            Star(name = "Polaris (α Ursae Minoris)", type = "Yellow supergiant (F7Ib)", color = "Yellow-white",
                images = listOf("https://storage.googleapis.com/theskylive-static/website/sky/stars/star-images/4/424_800.jpg",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcThn8IttNdHbpz6NaaDkySvBElQvSny1igeLg&s",
                    "https://astropixels.com/stars/images/Polaris-01w.jpg",
                    "https://avatars.mds.yandex.net/i?id=785e90ce6b16a7b546ef8f8655c94753_l-5254505-images-thumbs&n=13",
                    "https://avatars.mds.yandex.net/i?id=a1586a26e4bd51104f5deaafbdb3dba6_l-5269099-images-thumbs&n=13",
                    "https://habrastorage.org/getpro/habr/post_images/340/c79/612/340c796125a1f4685d9c1691b83e8fcd.jpg",
                    "https://spacegid.com/wp-content/uploads/2013/02/Tumannost-vokrug-Polyarnoy.jpg"
                    )
            ),
            Star(name = "Vega (α Lyrae)", type = "Main sequence (A0V)", color = "Blue-white",
                images = listOf("https://dq0hsqwjhea1.cloudfront.net/2017-09-26_59ca59ed7c8cc_04_Vega_20170922_400mm_10exp_280seg_3200iso_PI_PS.jpg",
                    "https://dq0hsqwjhea1.cloudfront.net/2017-09-26_59ca59ed7c8cc_04_Vega_20170922_400mm_10exp_280seg_3200iso_PI_PS.jpg",
                    "https://cdn.britannica.com/30/126230-004-D416E13D/Vega-Spitzer-Space-Telescope.jpg",
                    "https://i.ytimg.com/vi/NIWjSyTRDNA/maxresdefault.jpg",
                    "https://i.vogueindustry.com/images/028/image-82923-2-j.webp",
                    "https://site-edu.ru/wp-content/uploads/2/9/2/292be07ad084e552e4842fafdd52482c.jpeg",
                    "https://yt3.googleusercontent.com/ytc/AIdro_nnTJFy7pNM-Whcwbf1SpKcURQdYMEYqJNglvSkIVPULw=s900-c-k-c0x00ffffff-no-rj"
                    )
            ),
            Star("Arcturus (α Boötis)", "Red giant (K1.5III)", "Orange", listOf(
                "https://www.obs-sirene.com/sites/default/files/imagerie/080701_-_arcturus_-_bitube_-_350d.jpg",
                "https://mir-s3-cdn-cf.behance.net/project_modules/max_3840/c284df177635309.64da2d5f2d66c.png",
                "https://muhrizal94.wordpress.com/wp-content/uploads/2014/06/11-e1403392393176.png"
            )),
            Star(name = "Rigel (β Orionis)", type = "Blue supergiant (B8Ia)", color = "Blue-white", listOf(
                "https://avatars.dzeninfra.ru/get-zen_doc/49107/pub_5bec11b0f45fc700a9a1882a_5bec12e1fa8aae00abb5b171/scale_1200",
                "https://sun9-78.userapi.com/impg/gmwwLq-W6YgsuUJXeSybmWz9DOskQ3VQn9AJHw/p6PxJLtVzOY.jpg?size=920x828&quality=95&sign=f677f8bdec432b69d5c5f73d132470e4&c_uniq_tag=UM7eKqbSZ91FkicRE8tWFPZ9FvBYGuGSn0G5pY4-nRU&type=album",
                "https://sun9-61.userapi.com/s/v1/ig2/BeL2soITqTbYJ92zUuUStZX_3XwN600-xh1i0x1j2GpcIn31XcFXThEcrzHWRXt4K5EoEda2Mdi-tmnLuNZ0JPc8.jpg?quality=95&blur=50,20&as=32x37,48x56,72x83,108x125,160x185,240x278,360x416,480x555,540x624,588x680&from=bu&u=KOTP7007BON-fDr65K7jkp4Px0r5FdfWAHA1gySWzjU&cs=588x680"
            )),
            Star(name = "Aldebaran (α Tauri)", type = "Red giant (K5+III)", color = "Orange", listOf(
                "https://i.pinimg.com/originals/3b/a0/3b/3ba03b7148963f9418a6490c89dcfd5d.jpg",
                "https://avatars.dzeninfra.ru/get-zen_doc/1888987/pub_5cf10ac303ae9400af7e5b0e_5cf127ac4d430f00ae45ecbb/scale_1200",
                "https://storage.googleapis.com/theskylive-static/website/sky/stars/finder-charts/aldebaran-alpha-tauri-finder-chart.png"
            )),
            Star(name = "Capella (α Aurigae)", type = "Yellow giant (G3III)", color = "Yellow", listOf(
                "https://i.pinimg.com/736x/5c/be/16/5cbe1666ae071a5fbe1a5032e3dc02e6.jpg",
                "http://www.newforestobservatory.com/wordpress/wp-content/gallery/quasarsandother/capella_nfo_0.jpg",
                "https://22century.ru/wp-content/uploads/2020/06/6-capella-alpha-aurigae-finder-chart-919x599.png"
            )),
            Star(name = "Antares (α Scorpii)", type = "Red supergiant (M1.5Iab-Ib)", color = "Red", listOf(
                "https://static.wikia.nocookie.net/ayakashi-ghost-guild/images/b/b3/Antares_star.jpg/revision/latest?cb=20131225191233",
                "https://www.vaticanobservatory.org/wp-content/uploads/2021/05/Antares-and-the-Sun.jpg",
                "https://s.yimg.com/ny/api/res/1.2/W0Dif94VbsaYIti4UnH6iw--/YXBwaWQ9aGlnaGxhbmRlcjt3PTk2MDtoPTc0Mw--/https://media.zenfs.com/en/popular_science_109/91a408c66989a3f0d740ab353664a32f"
            )),
            Star(name = "Procyon (α Canis Minoris)", type = "Subgiant (F5IV-V)", color = "White-yellow", listOf(
                "https://i.pinimg.com/originals/34/06/b2/3406b2f9091768aa6ab91483bf0128dd.jpg"
            )),
            Star(name = "Altair (α Aquilae)", type = "Main sequence (A7V)", color = "White", listOf(
                "https://i.pinimg.com/736x/d6/fd/24/d6fd242324be764a39b7a567aa123548.jpg"
            )),
            Star(name = "Deneb (α Cygni)", type = "Blue-white supergiant (A2Ia)", color = "Blue-white", listOf(
                "https://fedoroff.net/_ph/13/189391903.jpg"
            )),
            Star(name = "Spica (α Virginis)", type = "Binary system (B1V + B2IV)", color = "Blue-white", listOf(
                "https://upload.wikimedia.org/wikipedia/commons/c/c4/GJ_504.jpg"
            )),
            Star(name = "Fomalhaut (α Piscis Austrini)", type = "Main sequence (A3V)", color = "White", listOf(
                "https://i.pinimg.com/originals/5f/f2/b5/5ff2b579f27437b6178f0e559d75fa5e.gif"
            )),
            Star(name = "Castor (α Geminorum)", type = "Multiple star system (A1V + A2V)", color = "White", listOf(
                "https://i.pinimg.com/736x/22/d8/0a/22d80a2be0e811f7ce8d83ae99a37022.jpg"
            )),
            Star(name = "Pollux (β Geminorum)", type = "Orange giant (K0III)", color = "Orange", listOf(
                "https://avatars.dzeninfra.ru/get-zen_doc/271828/pub_66518a235827f07266db8ccf_66518a824241dd157f88e1ba/scale_1200"
            )),
            Star(name = "Dubhe (α Ursae Majoris)", type = "Giant (K0III)", color = "Yellow-orange", listOf(
                "https://fsd.multiurok.ru/html/2018/03/04/s_5a9bf37f013e5/img19.jpg"
            )),
            Star(name = "Merak (β Ursae Majoris)", type = "Main sequence (A1V)", color = "White", listOf(
                "https://upload.wikimedia.org/wikipedia/commons/0/02/Sun_to_Arcturus_comparison.jpg"
            )),
            Star(name = "Alkaid (η Ursae Majoris)", type = "Main sequence (B3V)", color = "Blue-white", listOf(
                "https://live.staticflickr.com/765/20679218142_235b09a441_b.jpg"
            )),
            Star(name = "Algol (β Persei)", type = "Eclipsing binary (B8V + K0IV)", color = "Blue-white", listOf(
                "https://cdn.mos.cms.futurecdn.net/SyqzWPTJykLP76SZbAqUq3-1200-80.jpg"
            )),
            Star(name = "Alpheratz (α Andromedae)", type = "Main sequence (B9p)", color = "Blue-white", listOf(
                "https://osr.org/wp-content/uploads/2016/04/alpha-andromedae-star.jpg"
            )),
            Star(name = "Regulus (α Leonis)", type = "Main sequence (B7V)", color = "Blue-white", listOf(
                "https://miro.medium.com/v2/resize:fit:1200/1*tj1tkeUawHRnuqKQXdS3Xg.png"
            )),
            Star(name = "Canopus (α Carinae)", type = "Yellow-white supergiant (F0Ib)", color = "Yellow-white", listOf(
                "https://avatars.mds.yandex.net/i?id=1ca3b306e97f233bc70005eb64fc7aeb_l-5468296-images-thumbs&n=13"
            )),
            Star(name = "Achernar (α Eridani)", type = "Main sequence (B6V)", color = "Blue-white", listOf(
                "https://i.pinimg.com/736x/59/31/a2/5931a2cdaf6dc45e33c996aa545832b2.jpg"
            )),
            Star(name = "Bellatrix (γ Orionis)", type = "Blue giant (B2III)", color = "Blue", listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b3/Bellatrix_DSS.png/800px-Bellatrix_DSS.png"
            )),
            Star(name = "Zeta Reticuli", type = "Main sequence (G2V)", color = "Yellow", listOf(
                "https://upload.wikimedia.org/wikipedia/commons/7/7d/Zeta_Reticuli.jpg"
            )),
            Star(name = "Eta Carinae", type = "Luminous blue variable (LBV)", color = "Blue", listOf(
                "https://avatars.mds.yandex.net/i?id=6628ed978c013de11c55db3f061722df_l-5241497-images-thumbs&n=13"
            )),
            Star(name = "R136a1", type = "Wolf-Rayet star (WNH)", color = "Blue-white", listOf(
                "https://avatars.mds.yandex.net/i?id=150b3cfb9704d92c38bb51559da902a3_l-5234483-images-thumbs&n=13"
            )),
            Star(name = "Proxima Centauri", type = "Red dwarf (M5.5Ve)", color = "Red", listOf(
                "https://prancer.physics.louisville.edu/astrowiki/images/8/8d/Proxima_rgb.jpg"
            )),
        )

        testData.forEach{star ->
            val newStarRef = db.collection("Stars").document()

            val starWithId = star.copy(id = newStarRef.id)

            newStarRef.set(starWithId)
                .addOnSuccessListener {
                    Log.d("Firestore", "Star ${star.name} added successfully with id ${newStarRef.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error adding star ${star.name}", e)
                }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

    SmallDBApplicationTheme {

        Greeting("Android")
    }
}

var authManager : AuthManager = AuthManager()

@Composable
fun AuthScreen(padding : PaddingValues, activity: MainActivity) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(padding)
        .fillMaxWidth()) {

        Text(
            text = "Welcome to the stars Wiki!",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            authManager.signIn(email, password) { success, msg ->
                message = msg
                if (success) {
                    val intent = Intent(activity, ListOfDataActivity::class.java)
                    activity.startActivity(intent)
                    activity.finish()
                }
            }
        },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign-in")
        }

        Button(onClick = {
            authManager.signUp(email, password) { success, msg ->
                message = msg
            }
        },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign-up")
        }


        Text(message, color = Color.Red)
    }
}
