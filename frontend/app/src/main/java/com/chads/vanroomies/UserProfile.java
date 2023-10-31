package com.chads.vanroomies;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private String id;
    private String name;
    private int age;
    private String preferences;
    private String imageString;

    public UserProfile(String id) {

        this.id = id;
        this.name = "";
        this.age = -1;
        this.preferences = "";
        this.imageString = "";
    }

    public UserProfile(String id, String name, int age, String preferences, String imageString) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.preferences = preferences;
        this.imageString = imageString;
    }

    public String getId() { return id; }
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getPreferences() {
        return preferences;
    }

    public String getImageString() {
        return imageString;
    }

    public void setId(String id) { this.id = id; }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }
}