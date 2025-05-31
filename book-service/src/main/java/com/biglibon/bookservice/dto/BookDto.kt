package com.biglibon.bookservice.dto

import com.biglibon.bookservice.model.Book

data class BookDto @JvmOverloads constructor(
        val bookId: BookIdDto? = null,
        val title: String,
        val bookYear: Int,
        val author: String,
        val pressName: String
) {
    companion object {
        @JvmStatic
        fun convert(from: Book): BookDto {
            return BookDto(
                    from.id?.let { BookIdDto.convert(it, from.isbn) },
                    from.title,
                    from.bookYear,
                    from.author,
                    from.pressName
            )
        }
    }
}

