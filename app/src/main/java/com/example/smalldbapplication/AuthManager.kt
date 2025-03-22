package com.example.smalldbapplication

import android.util.Log
import com.example.smalldbapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signUp(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null){
                        val db = Firebase.firestore
                        db.collection("Users")
                            .document(userId)
                            .set(
                                User(
                                    id = userId
                                )
                            )
                            .addOnSuccessListener {
                                onResult(true, "Регистрация успешна, профиль сохранён!")
                            }
                            .addOnFailureListener { e ->
                                onResult(false, "Ошибка сохранения профиля: ${e.message}")
                            }
                    }
                    onResult(true, "Регистрация успешна!")
                } else {
                    onResult(false, task.exception?.message ?: "Ошибка регистрации")
                }
            }
    }

    fun signIn(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Вход успешен!")
                } else {
                    onResult(false, task.exception?.message ?: "Ошибка входа")
                }
            }
    }

    fun signOut() {
        auth.signOut()
        Log.d("Auth", "Пользователь вышел")
    }
}