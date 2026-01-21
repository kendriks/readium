package com.example.readium.data.mapper

import com.example.readium.data.model.Book
import com.example.readium.data.remote.GoogleBookItem

fun GoogleBookItem.toBook(): Book {
    val volume = volumeInfo

    val isbn13 = volume.industryIdentifiers
        ?.firstOrNull { it.type == "ISBN_13" }
        ?.identifier

    val coverUrl = volume.imageLinks?.thumbnail
        ?.replace("http://", "https://")
        ?: volume.imageLinks?.smallThumbnail
            ?.replace("http://", "https://")

    return Book(
        title = volume.title.orEmpty(),
        authors = volume.authors ?: emptyList(),
        publisher = volume.publisher ?: "Editora desconhecida",
        publishedDate = volume.publishedDate,
        description = volume.description,
        categories = volume.categories ?: emptyList(),
        pageCount = volume.pageCount,
        isbn = isbn13,
        coverUrl = coverUrl,
    )
}
