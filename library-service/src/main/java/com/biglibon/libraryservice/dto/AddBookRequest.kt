package com.biglibon.libraryservice.dto

data class AddBookRequest constructor(
        val libraryId: String,
        val isbn: String
)