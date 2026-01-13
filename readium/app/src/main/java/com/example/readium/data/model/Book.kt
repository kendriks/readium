package com.example.readium.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

enum class BookStatus {
    TO_READ,
    READING,
    READ,
    FAVORITE
}

data class Book(

    // Firestore
    var id: String = "",

    // Dados bibliográficos (Google Books + cadastro manual)
    var title: String = "",
    var authors: List<String> = emptyList(),
    var isbn: String? = null,
    var coverUrl: String? = null,
    var publisher: String? = null,
    var publishedDate: String? = null,
    var categories: List<String> = emptyList(),
    var description: String? = null,
    var pageCount: Int? = null,

    // Dados do usuário
    var ownerId: String = "",
    var ownerDisplayName: String? = null,

    // Estado do livro
    var status: BookStatus = BookStatus.TO_READ,
    var availableForTrade: Boolean = false,
    var condition: String? = null,

    @ServerTimestamp
    var createdAt: Date? = null,

    // Campos locais (não persistidos)
    @get:Exclude
    @set:Exclude
    var localTemporaryField: String? = null

) : Serializable {

    /* ======= Métodos utilitários ======= */

    fun getMainAuthor(): String =
        authors.firstOrNull().orEmpty()

    fun hasCover(): Boolean =
        !coverUrl.isNullOrBlank()

    fun updateStatusFromLegacy(
        lido: Boolean?,
        lendo: Boolean?,
        queroLer: Boolean?,
        favorito: Boolean?
    ) {
        status = when {
            favorito == true -> BookStatus.FAVORITE
            lido == true -> BookStatus.READ
            lendo == true -> BookStatus.READING
            queroLer == true -> BookStatus.TO_READ
            else -> status
        }
    }
}