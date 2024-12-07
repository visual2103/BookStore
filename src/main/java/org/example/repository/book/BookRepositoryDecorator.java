package org.example.repository.book;

import org.example.repository.book.BookRepository;

public abstract class BookRepositoryDecorator implements BookRepository {
    protected BookRepository decoratedBookRepository ;
    public BookRepositoryDecorator (BookRepository bookRepository){
        decoratedBookRepository = bookRepository ;
    }

}
