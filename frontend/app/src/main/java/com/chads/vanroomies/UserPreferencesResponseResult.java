package com.chads.vanroomies;

public class UserPreferencesResponseResult {
    final static String TAG = "UserPreferencesResponseResult";

    private String userId;
    private String minPrice;
    private String maxPrice;
    private String housingType;
    private int roommateCount;
    private boolean petFriendly;
    private String smoking;
    private String partying;
    private String drinking;
    private String noise;
    private String gender;
    private String moveInDate;
    private int leaseLength;
    private LocationObj location;
    private String _id;

    private int __v;

    // ChatGPT Usage: No
    public String getUserId() { return userId; }

    // ChatGPT Usage: No
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // ChatGPT Usage: No
    public String getMinPrice() {
        return minPrice;
    }

    // ChatGPT Usage: No
    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    // ChatGPT Usage: No
    public String getMaxPrice() {
        return maxPrice;
    }

    // ChatGPT Usage: No
    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
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
    public int getRoommateCount() {
        return roommateCount;
    }

    // ChatGPT Usage: No
    public void setRoommateCount(int roommateCount) {
        this.roommateCount = roommateCount;
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
    public String getSmoking() {
        return smoking;
    }

    // ChatGPT Usage: No
    public void setSmoking(String smoking) {
        this.smoking = smoking;
    }

    // ChatGPT Usage: No
    public String getPartying() {
        return partying;
    }

    // ChatGPT Usage: No
    public void setPartying(String partying) {
        this.partying = partying;
    }

    // ChatGPT Usage: No
    public String getDrinking() {
        return drinking;
    }

    // ChatGPT Usage: No
    public void setDrinking(String drinking) {
        this.drinking = drinking;
    }

    // ChatGPT Usage: No
    public String getNoise() {
        return noise;
    }

    // ChatGPT Usage: No
    public void setNoise(String noise) {
        this.noise = noise;
    }

    // ChatGPT Usage: No
    public String getGender() {
        return gender;
    }

    // ChatGPT Usage: No
    public void setGender(String gender) {
        this.gender = gender;
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
    public int getLeaseLength() {
        return leaseLength;
    }

    // ChatGPT Usage: No
    public void setLeaseLength(int leaseLength) {
        this.leaseLength = leaseLength;
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
    public String get_id() {
        return _id;
    }

    // ChatGPT Usage: No
    public void set_id (String _id) {
        this._id = _id;
    }

    // ChatGPT Usage: No
    public int get__v() {
        return __v;
    }

    // ChatGPT Usage: No
    public void set__v(int __v) {
        this.__v = __v;
    }

    // ChatGPT Usage: No
    public static class LocationObj {
        private double latitude;
        private double longitude;
        private String _id;

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

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
