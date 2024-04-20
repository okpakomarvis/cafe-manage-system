package com.cafems.cafemanagementsystem.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ProductWrapper {
    Integer id;
    String name;
    Integer price;
    Integer categoryId;
    String description;
    String status;
    String categoryName;
    public ProductWrapper(){}

    public ProductWrapper(Integer id, String name, Integer price, Integer categoryId, String description, String status, String categoryName) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
        this.description = description;
        this.status = status;
        this.categoryName = categoryName;
    }
    public ProductWrapper(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProductWrapper(Integer id, String name, Integer price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
