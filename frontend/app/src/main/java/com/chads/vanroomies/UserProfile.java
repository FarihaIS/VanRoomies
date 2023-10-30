package com.chads.vanroomies;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private String userProfileId;
    private String userProfileName;
    private int userProfileAge;
    private String userProfilePreferences;
    private int userProfileImageId;

    public UserProfile(String id, String name, int age, String preferences, int imageId) {
        this.userProfileId = id;
        this.userProfileName = name;
        this.userProfileAge = age;
        this.userProfilePreferences = preferences;
        this.userProfileImageId = imageId;
    }

    public String getUserProfileId() { return userProfileId; }
    public String getUserProfileName() {
        return userProfileName;
    }

    public int getUserProfileAge() {
        return userProfileAge;
    }

    public String getUserProfilePreferences() {
        return userProfilePreferences;
    }

    public int getUserProfileImageId() {
        return userProfileImageId;
    }

    public void setUserProfileId(String id) { this.userProfileId = id; }
    public void setUserProfileName(String name) {
        this.userProfileName = name;
    }

    public void setUserProfileAge(int age) {
        this.userProfileAge = age;
    }

    public void setUserProfilePreferences(String preferences) {
        this.userProfilePreferences = preferences;
    }

    public void setUserProfileImageId(int imageId) {
        this.userProfileImageId = imageId;
    }
}
