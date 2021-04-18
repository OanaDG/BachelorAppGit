package com.example.bachelorapp.Model;

public class Cart {
    private String id, title, author, price, date, image, quantity, recommendationId;

    public Cart() {

    }

    public Cart(String id, String title, String author, String price, String date, String image, String quantity, String recommendationId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.date = date;
        this.image = image;
        this.quantity = quantity;
        this.recommendationId = recommendationId;
    }

    public String getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(String recommendationId) {
        this.recommendationId = recommendationId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
