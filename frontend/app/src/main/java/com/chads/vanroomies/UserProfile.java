package com.chads.vanroomies;

import android.os.Parcel;
import android.os.Parcelable;

public class UserProfile implements Parcelable {
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

    // Parcelable implementation methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(bio);
        dest.writeString(profilePicture);
    }

    // Parcelable Creator
    public static final Parcelable.Creator<UserProfile> CREATOR = new Parcelable.Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

    // Constructor for Parcelable
    private UserProfile(Parcel in) {
        _id = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        bio = in.readString();
        profilePicture = in.readString();
    }
}