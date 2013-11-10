package com.osacky.peek.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Photo")
public class Photo extends ParseObject{
    public Photo() {
        //default constructor
    }

    public String getSender() {
        return getString("sender");
    }

    public void setSender(String sender) {
        put("sender", sender);
    }

    public String getReceiver() {
        return getString("receiver");
    }

    public void setReceiver(String receiver) {
        put("receiver", receiver);
    }

    public Long getTime() {
        return getLong("time");
    }

    public void setTime(long time) {
        put("time", time);
    }

    public ParseFile getPhotoFile() {
        return getParseFile("photo");
    }

    public void setPhotoFile(ParseFile file) {
        put("photo", file);
    }
}
