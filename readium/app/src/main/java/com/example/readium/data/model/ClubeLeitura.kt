package com.example.readium.data.model

import java.util.Date
data class ClubeLeitura(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val isPrivate: Boolean = false,
    val ownerId: String = "",
    val ownerName: String = "",
    val members: List<String> = emptyList(),
    val pendingRequests: List<String> = emptyList(),
    val currentBookTitle: String? = null,
    val createdAt: Date = Date(),
    val bannerUrl: String? = null
)