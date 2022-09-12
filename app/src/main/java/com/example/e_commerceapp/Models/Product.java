package com.example.e_commerceapp.Models;

public class Product {
    private String productImage1,productImage2, productid, productShortDescription, productDetails, productTitle;
    private String newPrice, oldPrice, warranty, ratings, available, category, dateCreated, publisher;

    public Product() {
    }

    public Product(String productImage1, String productImage2, String productid, String productShortDescription, String productDetails, String productTitle, String newPrice, String oldPrice, String warranty, String ratings, String available, String category, String dateCreated, String publisher) {
        this.productImage1 = productImage1;
        this.productImage2 = productImage2;
        this.productid = productid;
        this.productShortDescription = productShortDescription;
        this.productDetails = productDetails;
        this.productTitle = productTitle;
        this.newPrice = newPrice;
        this.oldPrice = oldPrice;
        this.warranty = warranty;
        this.ratings = ratings;
        this.available = available;
        this.category = category;
        this.dateCreated = dateCreated;
        this.publisher = publisher;
    }

    public String getProductImage1() {
        return productImage1;
    }

    public void setProductImage1(String productImage1) {
        this.productImage1 = productImage1;
    }

    public String getProductImage2() {
        return productImage2;
    }

    public void setProductImage2(String productImage2) {
        this.productImage2 = productImage2;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getProductShortDescription() {
        return productShortDescription;
    }

    public void setProductShortDescription(String productShortDescription) {
        this.productShortDescription = productShortDescription;
    }

    public String getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(String productDetails) {
        this.productDetails = productDetails;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(String newPrice) {
        this.newPrice = newPrice;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
