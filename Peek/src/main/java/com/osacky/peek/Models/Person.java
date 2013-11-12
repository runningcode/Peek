package com.osacky.peek.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Person")
public class Person extends ParseObject {

    public Person() {
    }

    public String getPhone() {
        return getString("phone");
    }

    public void setPhone(String phone) {
        put("phone", phone);
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }
}
