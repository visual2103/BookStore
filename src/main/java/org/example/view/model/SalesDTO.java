package org.example.view.model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class SalesDTO {
    private StringProperty author; // pentru a se mapa corect în ObservableList
    private StringProperty title;
    private IntegerProperty quantity;
    private FloatProperty totalPrice;
    private ObjectProperty<LocalDate> saleDate;
    private ObjectProperty<Long> employeeId;


    public void setAuthor(String author) {
        authorProperty().set(author);
    }

    public String getAuthor() {
        return authorProperty().get();
    }

    public StringProperty authorProperty() {
        if (author == null) {
            author = new SimpleStringProperty(this, "author");
        }
        return author;
    }


    public void setTitle(String title) {
        titleProperty().set(title);
    }

    public String getTitle() {
        return titleProperty().get();
    }

    public StringProperty titleProperty() {
        if (title == null) {
            title = new SimpleStringProperty(this, "title");
        }
        return title;
    }


    public void setQuantity(int quantity) {
        quantityProperty().set(quantity);
    }

    public int getQuantity() {
        return quantityProperty().get();
    }

    public IntegerProperty quantityProperty() {
        if (quantity == null) {
            quantity = new SimpleIntegerProperty(this, "quantity");
        }
        return quantity;
    }


    public void setTotalPrice(float totalPrice) {
        totalPriceProperty().set(totalPrice);
    }

    public float getTotalPrice() {
        return totalPriceProperty().get();
    }

    public FloatProperty totalPriceProperty() {
        if (totalPrice == null) {
            totalPrice = new SimpleFloatProperty(this, "totalPrice");
        }
        return totalPrice;
    }

    public void setSaleDate(LocalDate saleDate) {
        saleDateProperty().set(saleDate);
    }

    public LocalDate getSaleDate() {
        return saleDateProperty().get();
    }

    public ObjectProperty<LocalDate> saleDateProperty() {
        if (saleDate == null) {
            saleDate = new SimpleObjectProperty<>(this, "saleDate");
        }
        return saleDate;
    }


    public void setEmployeeId(Long employeeId) {
        employeeIdProperty().set(employeeId);
    }
    public Long getEmployeeId() {
        return employeeIdProperty().get();
    }

    public ObjectProperty<Long> employeeIdProperty() {
        if (employeeId == null) {
            employeeId = new SimpleObjectProperty<>(this, "employeeId");
        }
        return employeeId;
    }

}
