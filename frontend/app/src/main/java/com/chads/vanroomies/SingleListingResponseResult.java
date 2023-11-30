package com.chads.vanroomies;

import java.util.List;

public class SingleListingResponseResult {

    private String _id;
    private String userId;
    private String title;
    private String description;
    private String housingType;
    private int rentalPrice;

    private String listingDate;
    private String moveInDate;
    private boolean petFriendly;
    private LocationObj location;
    private List<String> images;

    // ChatGPT Usage: No
    public String get_id() {
        return _id;
    }

    // ChatGPT Usage: No
    public void set_id(String _id) {
        this._id = _id;
    }

    // ChatGPT Usage: No
    public String getUserId() {
        return userId;
    }

    // ChatGPT Usage: No
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // ChatGPT Usage: No
    public String getTitle() {
        return title;
    }

    // ChatGPT Usage: No
    public void setTitle(String title) {
        this.title = title;
    }

    // ChatGPT Usage: No
    public String getDescription() {
        return description;
    }

    // ChatGPT Usage: No
    public void setDescription(String description) {
        this.description = description;
    }

    // ChatGPT Usage: No
    public String getHousingType() {
        return housingType;
    }

    // ChatGPT Usage: No
    public void setHousingType(String housingType) {
        this.housingType = housingType;
    }

    // ChatGPT Usage: No
    public int getRentalPrice() {
        return rentalPrice;
    }

    // ChatGPT Usage: No
    public void setRentalPrice(int rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    // ChatGPT Usage: No
    public String getListingDate() {
        return listingDate;
    }

    // ChatGPT Usage: No
    public void setListingDate(String listingDate) {
        this.listingDate = listingDate;
    }

    // ChatGPT Usage: No
    public String getMoveInDate() {
        return moveInDate;
    }

    // ChatGPT Usage: No
    public void setMoveInDate(String moveInDate) {
        this.moveInDate = moveInDate;
    }

    // ChatGPT Usage: No
    public Boolean getPetFriendly() {
        return petFriendly;
    }

    // ChatGPT Usage: No
    public void setPetFriendly(Boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    // ChatGPT Usage: No
    public LocationObj getLocation() {
        return location;
    }

    // ChatGPT Usage: No
    public void setLocation(LocationObj location) {
        this.location = location;
    }

    // ChatGPT Usage: No
    public List<String> getImages() {
        return images;
    }

    // ChatGPT Usage: No
    public void setImages(List<String> images) {
        this.images = images;
    }

    public static class LocationObj {
        private double latitude;
        private double longitude;
        private String _id;

        // ChatGPT Usage: No
        public double getLatitude() {
            return latitude;
        }

        // ChatGPT Usage: No
        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        // ChatGPT Usage: No
        public double getLongitude() {
            return longitude;
        }

        // ChatGPT Usage: No
        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        // ChatGPT Usage: No
        public String get_id() {
            return _id;
        }

        // ChatGPT Usage: No
        public void set_id(String _id) {
            this._id = _id;
        }
    }
}
