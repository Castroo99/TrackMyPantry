package com.progetto.trackingmypantry.retrofit;

public class Utente{

    private String username;

    private String email;

    private String password;

    private String id;

    private String dateCreation;

    private String dateUpdate;

    public Utente(String username, String email, String password, String id, String dateCreation, String dateUpdate) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.id = id;
        this.dateCreation = dateCreation;
        this.dateUpdate = dateUpdate;
    }

    public String getUsername() {
        return username;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
