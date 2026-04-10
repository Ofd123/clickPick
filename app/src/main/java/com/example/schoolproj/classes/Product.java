package com.example.schoolproj.classes;

import java.io.Serializable;

/**
 * Represents a product with its associated details such as price, store, and image.
 * Implements Serializable to allow passing between activities or fragments.
 */
public class Product implements Serializable
{
    /** Unique identifier for the product. */
    String product_id;
    /** The display name of the product. */
    String product_name;
    /** The price of the product as a numeric value. */
    Double price;
    /** URL to the product's image. */
    String image_url;
    /** Detailed description of the product. */
    String description;
    /** Name of the store selling the product. */
    String store_name;
    /** Direct URL to the product's page in the store. */
    String store_url;
    /** Physical or region-based location of the store. */
    String store_location;
    /** Additional metadata or details about the product (e.g., shipping info). */
    String other_details;
    /** The currency or type of price (e.g., USD, ILS). */
    String price_type;

    /**
     * Parameterized constructor for Product.
     * @param product_name Name of the product.
     * @param price Price of the product.
     * @param price_type Currency or price type.
     * @param image URL of the product image.
     * @param description Product description.
     * @param store_name Name of the store.
     * @param store_url URL to the store's product page.
     * @param store_location Location of the store.
     * @param other_details Additional product details.
     */
    public Product(String product_name, Double price, String price_type, String image, String description, String store_name, String store_url, String store_location, String other_details)
    {
        this.product_name = product_name;
        this.price = price;
        this.price_type = price_type;
        this.image_url = image;
        this.description = description;
        this.store_name = store_name;
        this.store_url = store_url;
        this.store_location = store_location;
        this.other_details = other_details;
    }

    /**
     * Default constructor for Firebase or serialization purposes.
     */
    public Product()
    {

    }

    /**
     * Constructor specifically including product_id.
     * @param product_id Unique ID of the product.
     * @param product_name Name of the product.
     * @param price Price of the product.
     * @param price_type Currency or price type.
     * @param description Product description.
     * @param image Image URL.
     * @param store_name Name of the store.
     * @param store_url Direct URL.
     * @param store_location Location.
     */
    public Product(String product_id, String product_name, Double price, String price_type, String description, String image, String store_name, String store_url, String store_location)
    {
        this.product_id = product_id;
        this.product_name = product_name;
        this.price = price;
        this.price_type = price_type;
        this.image_url = image;
        this.description = description;
        this.store_name = store_name;
        this.store_url = store_url;
        this.store_location = store_location;
    }

    /** @return The unique product ID. */
    public String getProduct_id() {
        return product_id;
    }

    /** @return The product name. */
    public String getProduct_name() {
        return product_name;
    }

    /** @param product_name The new name for the product. */
    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    /** @return The product price. */
    public Double getPrice() {
        return price;
    }

    /** @param price The new price for the product. */
    public void setPrice(Double price) {
        this.price = price;
    }

    /** @return The image URL. */
    public String getImageUrl() {
        return image_url;
    }

    /** @param image The new image URL. */
    public void setImageUrl(String image) {
        this.image_url = image;
    }

    /** @return The product description. */
    public String getDescription() {
        return description;
    }

    /** @param description The new description. */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return The store name. */
    public String getStore_name() {
        return store_name;
    }

    /** @param store_name The new store name. */
    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    /** @return The store URL. */
    public String getStore_url() {
        return store_url;
    }

    /** @param store_url The new store URL. */
    public void setStore_url(String store_url) {
        this.store_url = store_url;
    }

    /** @return The store location. */
    public String getStore_location() {
        return store_location;
    }

    /** @param store_location The new store location. */
    public void setStore_location(String store_location)
    {
        this.store_location = store_location;
    }

    /** @param other_details The new additional details. */
    public void setOther_details(String other_details)
    {
        this.other_details = other_details;
    }

    /** @return Additional product details. */
    public String getOther_details()
    {
        return other_details;
    }

    /** @return The price type or currency. */
    public String getPrice_type() {
        return price_type;
    }

    /** @param price_type The new price type. */
    public void setPrice_type(String price_type) {
        this.price_type = price_type;
    }
}
