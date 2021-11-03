package com.progetto.trackingmypantry.retrofit;

public class Products {

    private String id;

    private String name;

    private String description;

    private String barcode;

    private String img;

    private String userId;

    private Boolean test;

    private String dateCreation;

    private String dateUpdate;

    public Products(String id, String name, String description, String barcode, String img, String userId, Boolean test, String dateCreation, String dateUpdate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.barcode = barcode;
        this.img = img;
        this.userId = userId;
        this.test = test;
        this.dateCreation = dateCreation;
        this.dateUpdate = dateUpdate;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(String dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getTest() {
        return test;
    }

    public void setTest(Boolean test) {
        this.test = test;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
