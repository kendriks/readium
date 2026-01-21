package com.example.readium.repository

import com.example.readium.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class PostRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val postsRef = firestore.collection("posts")

    suspend fun addPost(post: Post) {
        val docRef = postsRef.document()
        val newPost = post.copy(id = docRef.id)
        docRef.set(newPost).await()
    }

    suspend fun fetchFeedPosts(
        userId: String,
        friendsIds: List<String>
    ): List<Post> {

        val allowedUsers = friendsIds + userId

        return firestore
            .collection("posts")
            .whereIn("ownerId", allowedUsers)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Post::class.java)
    }
}
