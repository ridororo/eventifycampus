package com.rido.eventifycampus.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rido.eventifycampus.model.User

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://eventifycampus-73a1e-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")

    fun login(email: String, orgPassword: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isEmpty() || orgPassword.isEmpty()) {
            onResult(false, "Email dan password tidak boleh kosong")
            return
        }
        auth.signInWithEmailAndPassword(email, orgPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun register(user: User, password: String, onResult: (Boolean, String?) -> Unit) {
        if (user.email.isEmpty() || password.isEmpty() || user.name.isEmpty()) {
            onResult(false, "Data tidak lengkap")
            return
        }
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    val newUser = user.copy(uid = uid)
                    database.child(uid).setValue(newUser)
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                onResult(false, dbTask.exception?.message)
                            }
                        }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUserUid(): String? = auth.currentUser?.uid
}
