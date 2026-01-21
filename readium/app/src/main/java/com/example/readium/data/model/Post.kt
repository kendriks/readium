package com.example.readium.data.model

import java.util.Date

data class Post(
    var id: String = "",
    val title: String = "",
    val content: String = "",
    val bookId: String = "",
    val rating: Int = 3,
    val ownerName: String = "Usuário",
    val ownerId: String = "",
    val createdAt: Date = Date(),
    val likes: Int = 0,
    val commentsCount: Int = 0
)
