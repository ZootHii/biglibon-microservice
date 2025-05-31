package com.biglibon.libraryservice.dto

data class BookDto @JvmOverloads constructor(
        val bookId: BookIdDto? = null,
        val title: String? = "",
        val bookYear: Int? = 0,
        val author: String? = "",
        val pressName: String? = ""
)