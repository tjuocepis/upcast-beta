package com.example.titusjuocepis.upcastbeta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by titusjuocepis on 6/3/16.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class BaseChannel {

    String title, color, type, host;
    int n_members;
    ArrayList<String> tags;
    ArrayList<String> usernames = new ArrayList<>();

    public BaseChannel() {}

    public BaseChannel(String type, String title, String color, ArrayList<String> tags, String host) {
        this.type = type;
        this.title = title;
        this.color = color;
        this.n_members = 1;
        this.tags = tags;
        this.host = host;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getColor() {
        return color;
    }

    public int getN_members() {
        return n_members;
    }

    public void upMemberCount() {
        n_members++;
    }

    @JsonIgnore
    public String getHost() {
        return host;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    @JsonIgnore
    public String getTagsString() {
        return Arrays.toString(tags.toArray());
    }

    @JsonIgnore
    public ArrayList<String> getUsernames() {
        return usernames;
    }

    @JsonIgnore
    public void addUsername(String username) {
        usernames.add(username);
    }
}
