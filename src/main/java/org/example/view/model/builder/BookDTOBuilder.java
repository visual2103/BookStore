package org.example.view.model.builder;

import org.example.model.builder.BookBuilder;
import org.example.view.model.BookDTO;

public class BookDTOBuilder {
    private BookDTO bookDTO ;
    public BookDTOBuilder(){
        bookDTO = new BookDTO() ;
    }

    public BookDTOBuilder setId(Long id) {
        bookDTO.setId(id);
        return this;
    }

    public BookDTOBuilder setAuthor(String author){
        bookDTO.setAuthor(author);
        return this ;
    }

    public BookDTOBuilder setTitle (String title){
        bookDTO.setTitle(title);
        return this ;
    }

    public BookDTOBuilder setQuantity(Integer quantity){
        bookDTO.setQuantity(quantity);
        return this ;
    }

    public BookDTOBuilder setPrice (Float price){
        bookDTO.setPrice(price);
        return this ;
    }


    public BookDTO build(){
        return bookDTO ;
    }
}
