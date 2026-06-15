package com.rido.eventifycampus.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.rido.eventifycampus.model.User

class ProfileRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://eventifycampus-73a1e-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")
    private val storage = FirebaseStorage.getInstance().getReference("profile_images")

    fun getUserProfile(onResult: (User?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(null)
        database.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                onResult(user)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(null)
            }
        })
    }

    fun updateUserProfile(user: User, onResult: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false, "User not logged in")
        database.child(uid).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun uploadProfileImage(imageUri: Uri, onResult: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false, "User not logged in")
        val fileRef = storage.child("$uid.jpg")

        fileRef.putFile(imageUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    database.child(uid).child("profileImageUrl").setValue(uri.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onResult(true, uri.toString())
                            } else {
                                onResult(false, task.exception?.message)
                            }
                        }
                }
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }
}
