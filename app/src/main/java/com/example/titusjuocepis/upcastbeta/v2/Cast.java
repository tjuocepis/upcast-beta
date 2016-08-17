package com.example.titusjuocepis.upcastbeta.v2;

/**
 * Created by titusjuocepis on 8/3/16.
 */
public class Cast {

    String author;
    String msg;

    public Cast() {}

    public Cast(String auth, String data) {
        author = auth;
        msg = data;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
