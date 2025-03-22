package com.example.smalldbapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smalldbapplication.model.User
import com.example.smalldbapplication.ui.theme.SmallDBApplicationTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmallDBApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppBackground(R.drawable.starr_back_profile) {
                        ProfileScreen(
                            innerPadding,
                            activity = this
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(innerPadding : PaddingValues, modifier: Modifier = Modifier.padding(innerPadding), activity: ProfileActivity){
    var user : User by remember { mutableStateOf(User()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        getUserFromFirestore { fetchedUser ->
            user = fetchedUser
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Edit Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            item {
                EditableTextField(label = "Name", value = user.name) {
                    user = user.copy(name = it)
                }
            }
            item {
                EditableTextField(label = "Email", value = user.email) {
                    user = user.copy(email = it)
                }
            }
            item {
                EditableTextField(label = "Phone", value = user.phone) {
                    user = user.copy(phone = it)
                }
            }
            item {
                EditableTextField(label = "City", value = user.city) {
                    user = user.copy(city = it)
                }
            }
            item {
                EditableTextField(label = "Country", value = user.country) {
                    user = user.copy(country = it)
                }
            }
            item {
                EditableTextField(label = "About myself", value = user.bio) {
                    user = user.copy(bio = it)
                }
            }
            item {
                EditableTextField(label = "Interests", value = user.interests) {
                    user = user.copy(interests = it)
                }
            }
            item {
                EditableTextFieldWithAge(user = user, onAgeChange = { updatedUser ->
                    user = updatedUser
                })
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Публичный профиль")
                    Switch(
                        checked = user.isPublic,
                        onCheckedChange = { user = user.copy(isPublic = it) }
                    )
                }
            }

            item {
                Button(
                    onClick = { saveUserToFirestore(user) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сохранить")
                }
            }

            item {
                Button(
                    onClick = {
                        authManager.signOut()
                        val intent = Intent(activity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        activity.startActivity(intent)
                        activity.finish()
                    },
                    modifier = Modifier
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
                        text = "Logout",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        Firebase.firestore.collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.orEmpty()).delete()
                        FirebaseAuth.getInstance().currentUser?.delete()?.addOnSuccessListener {
                            val intent = Intent(activity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                            activity.startActivity(intent)
                            activity.finish()
                        }

                    },
                    modifier = Modifier
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
                        text = "Delete User",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun EditableTextFieldWithAge(user: User, onAgeChange: (User) -> Unit) {
    var ageText by remember { mutableStateOf(user.age.toString()) }

    EditableTextField(
        label = "Age",
        value = ageText,
        onValueChange = {
            val newAge = it.toIntOrNull()

            if (newAge != null) {
                ageText = it
                onAgeChange(user.copy(age = newAge))
            } else {
                Log.w("EditableTextField", "Invalid age input")
            }
        }
    )
}

@Composable
fun EditableTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}

fun saveUserToFirestore(user: User) {
    val db = Firebase.firestore
    db.collection("Users")
        .document(user.id)
        .set(user)
        .addOnSuccessListener {
            Log.d("Firestore", "Профиль сохранён!")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Ошибка сохранения", e)
        }

}

fun getUserFromFirestore(onResult: (User) -> Unit){
    val db = Firebase.firestore
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    db.collection("Users")
        .document(userId)
        .get()
        .addOnSuccessListener { userDoc ->
            if (userDoc.exists()) {
                val user = userDoc.toObject(User::class.java)
                if (user != null) {
                    onResult(user)
                } else {
                    onResult(User(id = userId, email = ""))
                }
            } else {
                db.collection("Users")
                    .document(userId)
                    .set(User(id = userId, email = ""))
                onResult(User(id = userId, email = ""))
            }
        }
}
