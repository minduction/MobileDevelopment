package com.example.smalldbapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.smalldbapplication.model.UsersFavourites
import com.example.smalldbapplication.model.Star
import com.example.smalldbapplication.ui.theme.SmallDBApplicationTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FavouritesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent(){
            SmallDBApplicationTheme {
                Scaffold() { innerPadding ->
                    AppBackground(R.drawable.starry_back_fav) {
                        Favourites(innerPadding)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Favourites(innerPadding : PaddingValues, modifier: Modifier = Modifier.padding(innerPadding)){

    var userFavourites : UsersFavourites by remember { mutableStateOf(UsersFavourites()) }
    var favouriteStars : List<Star> by remember { mutableStateOf(listOf<Star>()) }

    LaunchedEffect(Unit) {
        getUserFavouritesFromFirestore { fetchedFavourites ->
            userFavourites = fetchedFavourites
            getFavouriteStars(userFavourites.favouriteStars){ stars ->
                favouriteStars = stars

            }
        }
    }

    var selectedItem by remember { mutableStateOf<Star?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(modifier = modifier) {
        VerticalList(favouriteStars) { selectedItem = it }
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

fun getUserFavouritesFromFirestore(onResult: (UsersFavourites) -> Unit){
    val db = Firebase.firestore
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    db.collection("m2mFavouritesUsers")
        .document(userId)
        .get()
        .addOnSuccessListener { userDoc ->
            if (userDoc.exists()) {
                val userFavourites = userDoc.toObject(UsersFavourites::class.java)
                if (userFavourites != null) {
                    onResult(userFavourites)

                } else {
                    onResult(UsersFavourites())
                }
            } else {
                db.collection("m2mFavouritesUsers")
                    .document(userId)
                    .set(UsersFavourites())

                onResult(UsersFavourites())
            }
        }
}

fun getFavouriteStars(ids : List<String>, onResult: (List<Star>) -> Unit){
    if (ids.isEmpty()) {
        onResult(emptyList())
        return
    }

    val db = Firebase.firestore

    db.collection("Stars")
        .whereIn("id", ids)
        .get()
        .addOnSuccessListener { querySnapshot ->
                val items = querySnapshot.documents.mapNotNull { it.toObject(Star::class.java) }
                onResult(items)
            }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Ошибка при получении избранных объектов", e)
            onResult(emptyList())
        }

}