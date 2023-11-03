package com.chads.vanroomies;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private String _id;
    private String firstName;
    private String lastName;
    private String bio;
    private String profilePicture;

    public UserProfile(String id) {
        this._id = id;
        this.firstName = "";
        this.bio = "";
        this.profilePicture = "";
    }

    public String get_id() { return _id; }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() { return lastName; }

    public String getBio() {
        return bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void set_id(String _id) { this._id = _id; }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}