package com.progetto.trackingmypantry.retrofit;

import java.util.List;

public class ProductsToken {

    private List<Products> products;

    private String token;

    public ProductsToken(List<Products> products, String token) {
        this.products = products;
        this.token = token;
    }

    public List<Products> getProducts() {
        return products;
    }

    public void setProducts(List<Products> products) {
        this.products = products;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
