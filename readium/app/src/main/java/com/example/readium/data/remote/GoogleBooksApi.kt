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
    val title: String?,
    val authors: List<String>?,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val categories: List<String>?,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    val smallThumbnail: String?,
    val thumbnail: String?
)

interface GoogleBooksApi {
    //Exemplo: https://www.googleapis.com/books/v1/volumes?q=isbn:9780143126560
    @GET("volumes")
    suspend fun searchByIsbn(@Query("q") query: String): GoogleBooksResponse

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