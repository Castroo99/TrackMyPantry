package com.progetto.trackingmypantry.retrofit;

public class VotesRequest {

    private String token;

    private Integer rating;

    private String productId;

    public VotesRequest(String token, Integer rating, String productId) {
        this.token = token;
        this.rating = rating;
        this.productId = productId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
