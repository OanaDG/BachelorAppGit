package com.example.bachelorapp.Model;

public class RecommendationIds {
    String orderId, bookId;

    public RecommendationIds() {

    }


    public RecommendationIds(String orderId, String recommendationId) {
        this.orderId = orderId;
        this.bookId = recommendationId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
