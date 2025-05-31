package com.biglibon.libraryservice.dto

data class BookIdDto @JvmOverloads constructor(
        val id: String? = "",
        val isbn: String? = ""
)