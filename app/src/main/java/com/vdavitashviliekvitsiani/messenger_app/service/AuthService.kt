package com.vdavitashviliekvitsiani.messenger_app.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.vdavitashviliekvitsiani.messenger_app.model.User

class AuthService {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    companion object {
        @Volatile
        private var INSTANCE: AuthService? = null

        fun getInstance(): AuthService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthService().also { INSTANCE = it }
            }
        }
    }

    fun getCurrentUser() = auth.currentUser

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun signUp(
        nickname: String,
        profession: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        // Check if nickname is unique first
        checkNicknameAvailability(nickname) { isAvailable ->
            if (!isAvailable) {
                onResult(false, "Nickname already exists")
                return@checkNicknameAvailability
            }

            // Create email from nickname for Firebase Auth
            val email = "${nickname.lowercase()}@messenger.app"

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        firebaseUser?.let { user ->
                            val newUser = User(
                                uid = user.uid,
                                nickname = nickname,
                                profession = profession,
                                profileImageUrl = "",
                                isOnline = true,
                                lastSeen = System.currentTimeMillis()
                            )

                            saveUserToDatabase(newUser) { success ->
                                onResult(success, if (success) null else "Failed to save user data")
                            }
                        } ?: onResult(false, "User creation failed")
                    } else {
                        onResult(false, task.exception?.message ?: "Registration failed")
                    }
                }
        }
    }

    fun signIn(nickname: String, password: String, onResult: (Boolean, String?) -> Unit) {
        val email = "${nickname.lowercase()}@messenger.app"

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateUserOnlineStatus(true)
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message ?: "Login failed")
                }
            }
    }

    fun signOut() {
        updateUserOnlineStatus(false)
        auth.signOut()
    }

    private fun checkNicknameAvailability(nickname: String, onResult: (Boolean) -> Unit) {
        usersRef.orderByChild("nickname")
            .equalTo(nickname)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onResult(!snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(false)
                }
            })
    }

    private fun saveUserToDatabase(user: User, onResult: (Boolean) -> Unit) {
        usersRef.child(user.uid)
            .setValue(user.toMap())
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
            }
    }

    fun getCurrentUserData(onResult: (User?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            usersRef.child(currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userMap = snapshot.value as? Map<String, Any>
                        val user = userMap?.let { User.fromMap(it) }
                        onResult(user)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onResult(null)
                    }
                })
        } else {
            onResult(null)
        }
    }

    private fun updateUserOnlineStatus(isOnline: Boolean) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val updates = mapOf(
                "isOnline" to isOnline,
                "lastSeen" to System.currentTimeMillis()
            )
            usersRef.child(user.uid).updateChildren(updates)
        }
    }
}