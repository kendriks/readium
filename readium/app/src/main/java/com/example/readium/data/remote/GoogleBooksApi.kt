package com.example.readium.data.remote

import retrofit2.http.GET
import retrofit2.http.Query


data class GoogleBooksResponse(
    val kind: String?,
    val totalItems: Int?,
    val items: List<GoogleBookItem>?
)

data class GoogleBookItem(
    val id: String?,
    val volumeInfo: VolumeInfo

)

data class VolumeInfo(
    // Títulos
    val title: String?,
    val subtitle: String?,

    // Autoria
    val authors: List<String>?,

    // Publicação
    val publisher: String?,
    val publishedDate: String?,
    val language: String?,

    // Conteúdo
    val description: String?,
    val categories: List<String>?,
    val pageCount: Int?,

    // Avaliação
    val averageRating: Double?,
    val ratingsCount: Int?,

    // Identificadores
    val industryIdentifiers: List<IndustryIdentifier>?,

    // Mídia
    val imageLinks: ImageLinks?,

    // Links úteis
    val previewLink: String?,
    val infoLink: String?,
    val canonicalVolumeLink: String?
)


data class ImageLinks(
    val smallThumbnail: String?,
    val thumbnail: String?
)

data class IndustryIdentifier(
    val type: String?,
    val identifier: String?
)

public fun GoogleBookItem.needsFallback(): Boolean {
    val volume = volumeInfo
    return volume.publisher.isNullOrBlank() ||
            volume.imageLinks?.thumbnail.isNullOrBlank()
}


interface GoogleBooksApi {
    //Exemplo: https://www.googleapis.com/books/v1/volumes?q=isbn:9780143126560
    @GET("volumes")
    suspend fun searchByIsbn(@Query("q") query: String): GoogleBooksResponse

    @GET("volumes")
    suspend fun searchByTitle(
        @Query("q") title: String,
        @Query("maxResults") maxResults: Int = 5
    ): GoogleBooksResponse

    companion object {
        fun create(): GoogleBooksApi {
            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/")
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()
            return retrofit.create(GoogleBooksApi::class.java)
        }
    }
}