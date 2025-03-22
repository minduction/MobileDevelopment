package com.example.smalldbapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smalldbapplication.enums.SearchField
import com.example.smalldbapplication.model.Star
import com.example.smalldbapplication.ui.theme.SmallDBApplicationTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListOfDataActivity : ComponentActivity() {

    private val starsList = mutableStateListOf<Star>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        FirebaseAuth.getInstance().currentUser
        getStarsFromFirestore()
        setContent {
            SmallDBApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    mainScreen(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        activity = this,
                        stars = starsList
                    )
                }
            }
        }
    }

    private fun getStarsFromFirestore(){
        val db = Firebase.firestore

        db.collection("Stars")
            .get()
            .addOnSuccessListener { result ->
                Log.d("Firestore", "Получено ${result.size()} документов")
                starsList.clear()
                val temp = result.toObjects(Star::class.java)
                starsList.addAll(temp.sortedBy { it.images.size }.reversed())
                starsList.forEach {
                    Log.d("Firestore", "Star: ${it.name}, id: ${it.id}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mainScreen(name: String, modifier: Modifier = Modifier, activity: ListOfDataActivity, stars: List<Star>) {
    var selectedItem by remember { mutableStateOf<Star?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var filteredStars : List<Star> by remember { mutableStateOf<List<Star>>(emptyList()) }

    AppBackground(R.drawable.starry_background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier)
        )
        {
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = {
                    newQuery ->
                    searchQuery = newQuery
                    filteredStars = filterStars(searchQuery, stars)
                }
            )
            Box(modifier = Modifier
                .weight(8f)
                .fillMaxWidth()
                ){
                VerticalList(filteredStars){selectedItem = it}
            }
            Box(modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
                ){
                Row(
                    modifier = Modifier.align(alignment = Alignment.BottomCenter)
                ){
                    Button(
                        onClick = {
                            val intent = Intent(activity, FavouritesActivity::class.java)
                            activity.startActivity(intent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Text(
                            text = "Favourites",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Button(
                        onClick = {
                            val intent = Intent(activity, ProfileActivity::class.java)
                            activity.startActivity(intent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Text(
                            text = "Profile",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
    }

    }
    if (selectedItem != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedItem = null },
            sheetState = sheetState
        ) {
            DetailsContent(selectedItem!!) {
                selectedItem = null
            }
        }
    }
}

@Composable
fun VerticalList(stars:List<Star>, onItemClick:(Star) -> Unit){
    LazyColumn(modifier = Modifier.fillMaxSize()
        ) {
        items(stars) {star ->
            Row(modifier = Modifier
                .clickable { onItemClick(star) }){
                AsyncImage(model = star.images[0],
                    contentDescription = "Star",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .heightIn(max = 100.dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Text(
                    text = star.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    fontSize = 30.sp,
                    color = Color.White
                )
            }

        }
    }
}

@Composable
fun DetailsContent(item:Star, onClose: () -> Unit){
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Detailed info", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "You have chosen ${item.name}")

        Spacer(modifier = Modifier.height(20.dp))
        ItemImagesList(item.images)
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { addStarToFavourites(item.id) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add to favourites")
        }
        Button(
            onClick = { removeStarFromFavourites(item.id) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Remove from favourites")
        }

    }
}

fun addStarToFavourites(starId : String){
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val db = Firebase.firestore
    val doc = db.collection("m2mFavouritesUsers").document(userId)

    doc.get().addOnSuccessListener { document ->
        if (document.exists()) {

            doc.update("favouriteStars", FieldValue.arrayUnion(starId))
                .addOnSuccessListener {
                    Log.d("Firestore", "Звезда добавлена в избранное!")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Ошибка добавления в избранное", e)
                }
        } else {

            val userFavorites = hashMapOf(
                "favouriteStars" to listOf(starId)
            )
            doc.set(userFavorites)
                .addOnSuccessListener {
                    Log.d("Firestore", "Документ создан и звезда добавлена в избранное!")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Ошибка создания документа и добавления в избранное", e)
                }
        }
    }
}

fun removeStarFromFavourites(starId : String){
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val db = Firebase.firestore

    db.collection("m2mFavouritesUsers").document(userId)
        .update("favouriteStars", FieldValue.arrayRemove(starId))
        .addOnSuccessListener {
            Log.d("Firestore", "Звезда удалена из избранного!")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Ошибка удаления из избранного", e)
        }
}

@Composable
fun ItemImagesList(images:List<String>, modifier: Modifier = Modifier){

    LazyRow(modifier = modifier.fillMaxWidth()) {
        items(images) {url ->
            AsyncImage(model = url,
                contentDescription = "Star",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(400.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
fun AppBackground(imageResId : Int, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "App Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        content()
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        label = { Text("Search") },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        }
    )
}

fun filterStars(queryName : String, inputStars : List<Star>, field : String = SearchField.NAME.toString()) : List<Star>{
    return inputStars.filter { star -> star.name.contains(queryName, ignoreCase = true) }
}
