package com.osacky.peek.Models;

import java.util.ArrayList;
import java.util.List;

public class Contact {
    final private String name;
    private List<String> phones;

    public Contact(String name) {
        this.name = name;
        phones = new ArrayList<String>();
    }

    public void addPhone(String phone) {
        phones.add(phone);
    }

    public String getName() {
        return name;
    }
}
