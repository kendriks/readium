package com.example.readium.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

enum class BookStatus { TO_READ, READING, READ, FAVORITE }

data class Book(
    var id: String = "",

    // Dados bibliográficos
    var title: String = "",
    var authors: List<String> = emptyList(),
    var isbn: String? = null,
    var coverUrl: String? = null,
    var publisher: String? = null,
    var publishedDate: String? = null,
    var categories: List<String> = emptyList(),
    var description: String? = null,

    // Dados de propriedade
    var ownerId: String = "",            //quem cadastrou o livro (uid do FirebaseAuth)
    var ownerDisplayName: String? = null,
    var status: BookStatus = BookStatus.TO_READ,
    var availableForTrade: Boolean = false,
    var condition: String? = null,       //"Novo", "Bom", "Usado", etc.

    @ServerTimestamp
    var createdAt: Date? = null,


    @get:Exclude
    @set:Exclude
    var localTemporaryField: String? = null
)
