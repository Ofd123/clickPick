package com.example.schoolproj.classes;

import java.io.Serializable;

public class Product implements Serializable
{
    String product_id;
    String product_name;
    Double price;
    String image_url;
    String description;
    String store_name;
    String store_url;
    String store_location;
    String other_details;

    public Product(String product_name, Double price, String image, String description, String store_name, String store_url, String store_location, String other_details)
    {
        this.product_name = product_name;
        this.price = price;
        this.image_url = image;
        this.description = description;
        this.store_name = store_name;
        this.store_url = store_url;
        this.store_location = store_location;
        this.other_details = other_details;
    }

    public Product()
    {

    }

    public Product(String product_id, String product_name, Double price, String description, String image, String store_name, String store_url, String store_location)
    {
        this.product_id = product_id;
        this.product_name = product_name;
        this.price = price;
        this.image_url = image;
        this.description = description;
        this.store_name = store_name;
        this.store_url = store_url;
        this.store_location = store_location;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(String image) {
        this.image_url = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_url() {
        return store_url;
    }

    public void setStore_url(String store_url) {
        this.store_url = store_url;
    }

    public String getStore_location() {
        return store_location;
    }

    public void setStore_location(String store_location)
    {
        this.store_location = store_location;
    }
    public void setOther_details(String other_details)
    {
        this.other_details = other_details;
    }
    public String getOther_details()
    {
        return other_details;
    }



}
