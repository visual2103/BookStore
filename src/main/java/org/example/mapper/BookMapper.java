package org.example.mapper;

import org.example.model.Book;
import org.example.model.builder.BookBuilder;
import org.example.view.model.BookDTO;
import org.example.view.model.builder.BookDTOBuilder;

import java.time.LocalDate;
import java.util.List;

public class BookMapper {
    //mapare intre book -> BookDTO
    public static BookDTO convertBookToBookDTO(Book book) {
        return new BookDTOBuilder()
                .setId(book.getId())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setQuantity(book.getQuantity())
                .setPrice(book.getPrice())
                .build();
    }

    public static Book convertBookDTOToBook(BookDTO bookDTO) {
        return new BookBuilder()
                .setId(bookDTO.getId())  // Setează ID-ul aici
                .setTitle(bookDTO.getTitle())
                .setAuthor(bookDTO.getAuthor())
                .setQuantity(bookDTO.getQuantity())
                .setPrice(bookDTO.getPrice())
                .build();
    }

    public static List<BookDTO> convertBookListToBookDTOList(List<Book> books){
        return books.parallelStream().map(BookMapper::convertBookToBookDTO).toList() ;
    }

    public static List<Book> convertBookDTOListToBookList(List<BookDTO> bookDTOS){
        return bookDTOS.parallelStream().map(BookMapper::convertBookDTOToBook).toList() ;
    }
}
