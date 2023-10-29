package com.chads.vanroomies;

public class MatchModal {
    private String matchName;
    private int matchAge;
    private String matchPreferences;
    private int matchImageId;

    public MatchModal(String name, int age, String preferences, int imageId) {
        this.matchName = name;
        this.matchAge = age;
        this.matchPreferences = preferences;
        this.matchImageId = imageId;
    }

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
