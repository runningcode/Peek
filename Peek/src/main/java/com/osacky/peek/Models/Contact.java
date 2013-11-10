package com.osacky.peek.Models;

import com.parse.ParseUser;


public class Contact {

    final private String name;
    final private String phone;
    private ParseUser user;
    private String photoURI;

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setUser(ParseUser user) {
        this.user = user;
    }

    public ParseUser getUser() {
        return user;
    }

    public String getPhotoURI() {
        return photoURI;
    }

    public void setPhotoURI(String photoURI) {
        this.photoURI = photoURI;
    }
}
