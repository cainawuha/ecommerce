package com.how2java.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Entity
@Table(name = "category")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })

public class Category{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;


    @Column(name="parent_cid")
    int parent_cid;

    String name;

    @Transient
   List<Product> products;
    @Transient
    CopyOnWriteArrayList<CopyOnWriteArrayList<Product>> productsByRow;



    @Transient
    CopyOnWriteArrayList<Category> children = new CopyOnWriteArrayList<>();

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Product> getProducts() {
        return products;
    }
    public void setProducts(List<Product> products) {

        this.products =  products;
    }
    public CopyOnWriteArrayList<CopyOnWriteArrayList<Product>> getProductsByRow() {
        return productsByRow;
    }

    public void setProductsByRow(CopyOnWriteArrayList<CopyOnWriteArrayList<Product>> productsByRow) {
        this.productsByRow =  productsByRow;
    }
    public int getParent_cid() {
        return parent_cid;
    }

    public void setParent_cid(int parent_cid) {
        this.parent_cid = parent_cid;
    }
    public CopyOnWriteArrayList<Category> getChildren() {
        return (CopyOnWriteArrayList<Category>) children;
    }

    public void setChildren(List<Category> children) {
        this.children = (CopyOnWriteArrayList<Category>) children;
    }
    @Override
    public String toString() {
        return "Category [id=" + id + ", name=" + name + "]";
    }
}

