package com.example.readium.data.model

data class ThematicList(
    val id: String = "",
    val nome: String = "",
    val descricao: String = "",
    val livros: List<Book> = emptyList()
) {
    constructor() : this("", "", "", emptyList())
}
