package com.progetto.trackingmypantry.retrofit;

public class ProductRequest {
    private String token;

    private String name;

    private String description;

    String barcode;

    private String img;

    private Boolean test;

    public ProductRequest(String token, String name, String description, String barcode, String img, Boolean test) {
        this.token = token;
        this.name = name;
        this.description = description;
        this.barcode = barcode;
        this.img = img;
        this.test = test;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getTest() {
        return test;
    }

    public void setTest(Boolean test) {
        this.test = test;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getImage() {
        return img;
    }

    public void setImage(String image) {
        this.img = img;
    }
}
