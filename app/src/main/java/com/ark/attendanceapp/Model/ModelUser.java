package com.ark.attendanceapp.Model;

public class ModelUser {
    private String username;
    private String email;
    private String role;
    private String url_photo;
    private String number_phone;
    private String key;


    public ModelUser(){

    }

    public ModelUser(String username, String email, String role, String url_photo, String number_phone) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.url_photo = url_photo;
        this.number_phone = number_phone;
    }

    public String getUsername() {
        return username;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUrl_photo() {
        return url_photo;
    }

    public void setUrl_photo(String url_photo) {
        this.url_photo = url_photo;
    }

    public String getNumber_phone() {
        return number_phone;
    }

    public void setNumber_phone(String number_phone) {
        this.number_phone = number_phone;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
