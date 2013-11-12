package com.osacky.peek.Models;

public class Contact {

    final private String name;
    final private String phone;
    private Person person;
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

    public void setUser(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public String getPhotoURI() {
        return photoURI;
    }

    public void setPhotoURI(String photoURI) {
        this.photoURI = photoURI;
    }
}
