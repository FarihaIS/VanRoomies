package com.chads.vanroomies;

public class MatchModal {
    // Variables
    private String matchName;
    private int matchAge;
    private String matchPreferences;
    private int matchImageId;

    // Constructor
    public MatchModal(String name, int age, String preferences, int imageId) {
        this.matchName = name;
        this.matchAge = age;
        this.matchPreferences = preferences;
        this.matchImageId = imageId;
    }

    // Getters
    public String getMatchName() {
        return matchName;
    }

    public int getMatchAge() {
        return matchAge;
    }

    public String getMatchPreferences() {
        return matchPreferences;
    }

    public int getMatchImageId() {
        return matchImageId;
    }

    // Setters
    public void setMatchName(String name) {
        this.matchName = name;
    }

    public void setMatchAge(int age) {
        this.matchAge = age;
    }

    public void setMatchPreferences(String preferences) {
        this.matchPreferences = preferences;
    }

    public void setMatchImageId(int imageId) {
        this.matchImageId = imageId;
    }
}
