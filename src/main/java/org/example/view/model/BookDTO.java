package org.example.view.model;

import javafx.beans.property.*;

public class BookDTO {

    private LongProperty id;

    public void setId(Long id) {
        idProperty().set(id);
    }

    public Long getId() {
        return idProperty().get();
    }

    private LongProperty idProperty() {
        if (id == null) {
            id = new SimpleLongProperty(this, "id");
        }
        return id;
    }

    private StringProperty title ;

    public void setTitle(String title) {
        titleProperty().set(title);
    }

    public String getTitle() {
        return titleProperty().get();
    }

    private StringProperty titleProperty(){
        if (title == null){
            title = new SimpleStringProperty(this , "title") ;
        }
        return title;
    }
    private StringProperty author ;

    public void setAuthor(String author) {
        authorProperty().set(author);
    }
    public String getAuthor() {
        return authorProperty().get();
    }
    private StringProperty authorProperty(){
        if (author == null){
            author = new SimpleStringProperty(this , "author") ;
        }
        return author;
    }


    private IntegerProperty quantity ;

    public void setQuantity(Integer quantity) {
        quantityProperty().set(quantity);
    }
    public Integer getQuantity() {
        return quantityProperty().get();
    }
    private IntegerProperty quantityProperty(){
        if (quantity == null){
            quantity = new SimpleIntegerProperty(this , "quantity") ;
        }
        return quantity;
    }

    private FloatProperty price ;

    public void setPrice(Float price) {
        priceProperty().set(price);
    }
    public Float getPrice() {
        return priceProperty().get();
    }
    private FloatProperty priceProperty(){
        if (price == null){
            price = new SimpleFloatProperty(this , "price") ;
        }
        return price;
    }

}