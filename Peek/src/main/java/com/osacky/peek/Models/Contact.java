package com.osacky.peek.Models;

import com.parse.ParseUser;

public class Contact {
    final private String name;
    final private String phone;
    final private ParseUser user;

    public Contact(String name, String phone, ParseUser user) {
        this.name = name;
        this.phone = phone;
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public ParseUser getUser() {
        return user;
    }


}
