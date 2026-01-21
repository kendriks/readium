package com.example.readium.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.readium.data.User
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FriendsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    var friends = mutableStateListOf<User>()
        private set

    val friendsCount: Int
        get() = friends.size
    var isLoading by mutableStateOf(false)
        private set

    fun loadFriends(userId: String) {
        isLoading = true
        friends.clear()

        db.collection("friends")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->

                val friendIds =
                    doc.get("ids_friends") as? List<String> ?: emptyList()

                if (friendIds.isEmpty()) {
                    isLoading = false
                    return@addOnSuccessListener
                }

                val chunks = friendIds.chunked(10)
                var completed = 0

                chunks.forEach { chunk ->
                    db.collection("users")
                        .whereIn(FieldPath.documentId(), chunk)
                        .get()
                        .addOnSuccessListener { result ->

                            val list = result.documents.mapNotNull {
                                val user = it.toObject(User::class.java)
                                user?.id = it.id
                                user
                            }

                            friends.addAll(list)

                            completed++
                            if (completed == chunks.size) {
                                isLoading = false
                            }
                        }
                        .addOnFailureListener {
                            completed++
                            if (completed == chunks.size) {
                                isLoading = false
                            }
                        }
                }
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    fun removeFriend(userId: String, friend: User) {
        val friendId = friend.id ?: return

        isLoading = true

        val currentUserRef = db.collection("friends").document(userId)
        val friendRef = db.collection("friends").document(friendId)

        db.runBatch { batch ->

            batch.update(
                currentUserRef,
                "ids_friends",
                FieldValue.arrayRemove(friendId)
            )

            batch.update(
                friendRef,
                "ids_friends",
                FieldValue.arrayRemove(userId)
            )

        }.addOnSuccessListener {
            friends.removeAll { it.id == friendId }
            isLoading = false
        }.addOnFailureListener {
            isLoading = false
        }
    }
}
