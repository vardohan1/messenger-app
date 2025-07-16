package com.vdavitashviliekvitsiani.messenger_app.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vdavitashviliekvitsiani.messenger_app.model.User

class AuthService {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    companion object {
        @Volatile
        private var INSTANCE: AuthService? = null

        fun getInstance(): AuthService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthService().also { INSTANCE = it }
            }
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun isUserLoggedIn(): Boolean {
        val result = auth.currentUser != null
        Log.d("AuthService", "isUserLoggedIn() = $result, currentUser = ${auth.currentUser}")
        return result
    }

    fun signUp(nickname: String, profession: String, password: String, onResult: (Boolean, String?) -> Unit) {
        Log.d("AuthService", "Starting signUp for nickname: $nickname")

        database.child("users").orderByChild("nickname").equalTo(nickname)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("AuthService", "Nickname check completed, exists: ${snapshot.exists()}")

                    if (snapshot.exists()) {
                        Log.d("AuthService", "Nickname already exists")
                        onResult(false, "Nickname already exists")
                    } else {
                        Log.d("AuthService", "Nickname available, creating Firebase Auth account")

                        val email = "${nickname}@messenger.app"
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                Log.d("AuthService", "Firebase Auth completed: ${task.isSuccessful}")

                                if (task.isSuccessful) {
                                    val user = task.result?.user
                                    Log.d("AuthService", "Firebase user created: ${user?.uid}")

                                    user?.let { firebaseUser ->
                                        val userData = User(
                                            uid = firebaseUser.uid,
                                            nickname = nickname,
                                            profession = profession,
                                            profileImageUrl = "",
                                            isOnline = true
                                        )

                                        Log.d("AuthService", "Saving user data to database: $userData")

                                        database.child("users").child(firebaseUser.uid).setValue(userData.toMap())
                                            .addOnCompleteListener { dbTask ->
                                                Log.d("AuthService", "Database save completed: ${dbTask.isSuccessful}")

                                                if (dbTask.isSuccessful) {
                                                    Log.d("AuthService", "Sign up successful!")
                                                    onResult(true, null)
                                                } else {
                                                    Log.e("AuthService", "Database save failed: ${dbTask.exception?.message}")
                                                    onResult(false, dbTask.exception?.message ?: "Failed to save user data")
                                                }
                                            }
                                    } ?: run {
                                        Log.e("AuthService", "Failed to get user data from Firebase Auth")
                                        onResult(false, "Failed to get user data")
                                    }
                                } else {
                                    Log.e("AuthService", "Firebase Auth failed: ${task.exception?.message}")
                                    onResult(false, task.exception?.message ?: "Authentication failed")
                                }
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AuthService", "Database nickname check failed: ${error.message}")
                    onResult(false, "Database error: ${error.message}")
                }
            })
    }

    fun signIn(nickname: String, password: String, onResult: (Boolean, String?) -> Unit) {
        Log.d("AuthService", "Starting signIn for nickname: $nickname")

        val email = "${nickname}@messenger.app"

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                Log.d("AuthService", "Firebase signIn completed: ${task.isSuccessful}")

                if (task.isSuccessful) {
                    Log.d("AuthService", "Sign in successful!")
                    onResult(true, null)
                } else {
                    Log.e("AuthService", "Sign in failed: ${task.exception?.message}")
                    onResult(false, task.exception?.message ?: "Sign in failed")
                }
            }
    }

    fun getCurrentUserData(onResult: (User?) -> Unit) {
        Log.d("AuthService", "getCurrentUserData called")
        val currentUser = auth.currentUser
        Log.d("AuthService", "currentUser = ${currentUser?.uid}")

        if (currentUser != null) {
            database.child("users").child(currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("AuthService", "Database query completed, exists = ${snapshot.exists()}")

                        if (snapshot.exists()) {
                            val userMap = snapshot.value as? Map<String, Any>
                            val user = userMap?.let { User.fromMap(it) }
                            Log.d("AuthService", "User found: $user")
                            onResult(user)
                        } else {
                            Log.d("AuthService", "No user data found in database")
                            onResult(null)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("AuthService", "Database error: ${error.message}")
                        onResult(null)
                    }
                })
        } else {
            Log.d("AuthService", "No current user in Firebase Auth")
            onResult(null)
        }
    }

    fun updateUserProfile(nickname: String, profession: String, onComplete: (Boolean, String?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onComplete(false, "No user logged in")
            return
        }

        getCurrentUserData { currentUser ->
            if (currentUser?.nickname != nickname) {
                database.child("users").orderByChild("nickname").equalTo(nickname)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val existingUserId = snapshot.children.firstOrNull()?.key
                                if (existingUserId != userId) {
                                    onComplete(false, "Nickname already taken")
                                    return
                                }
                            }

                            saveUpdatedProfile(userId, nickname, profession, onComplete)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            onComplete(false, "Database error: ${error.message}")
                        }
                    })
            } else {
                saveUpdatedProfile(userId, nickname, profession, onComplete)
            }
        }
    }

    private fun saveUpdatedProfile(userId: String, nickname: String, profession: String, onComplete: (Boolean, String?) -> Unit) {
        val updates = hashMapOf<String, Any>(
            "nickname" to nickname,
            "profession" to profession
        )

        var currUser = auth.currentUser
        currUser?.updateEmail("${nickname}@messenger.app")

        database.child("users")
            .child(userId)
            .updateChildren(updates)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                Log.e("AuthService", "Error updating profile", e)
                onComplete(false, "Failed to update profile")
            }
    }

    fun signOut() {
        Log.d("AuthService", "Signing out")
        auth.signOut()
    }
    fun updateUserProfileImage(imageUrl: String, onComplete: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        val updates = hashMapOf<String, Any>(
            "profileImageUrl" to imageUrl
        )
        if (currentUser != null) {
            database.child("users")
                .child(currentUser.uid)
                .updateChildren(updates)
                .addOnSuccessListener {
                    onComplete(true)
                }
                .addOnFailureListener {
                    onComplete(false)
                }
        } else {
            onComplete(false)
        }
    }
}