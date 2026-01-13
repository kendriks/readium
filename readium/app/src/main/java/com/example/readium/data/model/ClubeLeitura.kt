package com.example.readium.data.model

data class ClubeLeitura(
    val id: String = "",
    val nomeClube: String = "",
    val descricaoClube: String = "",
    val publico: Boolean = true,
    val Livro: Book? = null,
    val membros: List<String> = emptyList()
) {
    constructor() : this("", "", "", true, null, emptyList())
}

