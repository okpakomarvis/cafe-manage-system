package com.cafems.cafemanagementsystem.pojo;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@org.hibernate.annotations.NamedQuery(name="Product.getAllProduct",
        query = "select new com.cafems.cafemanagementsystem.wrapper.ProductWrapper(p.id, p.name,p.price, p.category.id, p.description, p.status,p.category.name) from Product p")
@org.hibernate.annotations.NamedQuery(name="Product.updateProductStatus",
        query = "update Product p set p.status=:status where p.id=:id")
@org.hibernate.annotations.NamedQuery(name = "Product.getProductByCategory",
        query = "select new com.cafems.cafemanagementsystem.wrapper.ProductWrapper(p.id, p.name) from Product p where p.category.id=:id and p.status='true'")
@org.hibernate.annotations.NamedQuery(name = "Product.getProductById",
        query = "select new com.cafems.cafemanagementsystem.wrapper.ProductWrapper(p.id, p.name, p.price, p.description) from Product p where p.id=:id")
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "product")
public class Product implements Serializable {
    private static final long serialVersionUID =1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name ="name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_fk", nullable = true)
    private Category category;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "status")
    private String status;


}
