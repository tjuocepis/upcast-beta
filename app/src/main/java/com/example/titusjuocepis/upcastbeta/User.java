package com.example.titusjuocepis.upcastbeta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by titusjuocepis on 6/4/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String key, email, dateCreated;

    public User() {}

    public User(String email, String date_created) {
        this.email = email;
        String[] tokens = email.split("\\@");
        this.key = tokens[0];
        this.dateCreated = date_created;
    }

    public String getEmail() {
        return email;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    @JsonIgnore
    public String getKey() {
        return key;
    }
}
