package com.chads.vanroomies;

public class UserPreferencesResponseResult {

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



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMinPrice() {
        return minPrice;
    }
    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }
    public String getMaxPrice() {
        return maxPrice;
    }
    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }
    public String getHousingType() {
        return housingType;
    }

    public void setHousingType(String housingType) {
        this.housingType = housingType;
    }


    public int getRoommateCount() {
        return roommateCount;
    }

    public void setRoommateCount(int roommateCount) {
        this.roommateCount = roommateCount;
    }

    public Boolean getPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(Boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public String getSmoking() {
        return smoking;
    }

    public void setSmoking(String smoking) {
        this.smoking = smoking;
    }

    public String getPartying() {
        return partying;
    }

    public void setPartying(String partying) {
        this.partying = partying;
    }

    public String getDrinking() {
        return drinking;
    }

    public void setDrinking(String drinking) {
        this.drinking = drinking;
    }

    public String getNoise() {
        return noise;
    }

    public void setNoise(String noise) {
        this.noise = noise;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(String moveInDate) {
        this.moveInDate = moveInDate;
    }

    public int getLeaseLength() {
        return leaseLength;
    }

    public void setLeaseLength(int leaseLength) {
        this.leaseLength = leaseLength;
    }
    public LocationObj getLocation() {
        return location;
    }

    public void setLocation(LocationObj location) {
        this.location = location;
    }
    public String get_id(String _id) { return _id; }
    public void set_id (String _id) { this._id = _id; }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public static class LocationObj {
        private int latitude;
        private int longitude;
        private String _id;

        public int getLatitude() {
            return latitude;
        }

        public void setLatitude(int latitude) {
            this.latitude = latitude;
        }

        public int getLongitude() {
            return longitude;
        }

        public void setLongitude(int longitude) {
            this.longitude = longitude;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }
    }
}