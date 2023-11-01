package com.chads.vanroomies;

import java.util.HashMap;

// Reference: https://www.geeksforgeeks.org/recyclerview-using-gridlayoutmanager-in-android-with-example/
public class ListingsRecyclerData {
    private String title;
    private String imageString;
    private String listingId;
    private HashMap<String, String> additionalInfo;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(int imgId) {
        this.imageString = imageString;
    }
    public String getListingId() {
        return listingId;
    }
    public void setListingId(String id) {
        this.listingId = id;
    }
    public HashMap<String, String> getAdditionalInfo() {
        return additionalInfo;
    }
    public void setAdditionalInfo(HashMap<String, String> info) {
        this.additionalInfo = info;
    }

    public ListingsRecyclerData(String title, String imageString, String listingId, HashMap<String, String> additionalInfo) {
        this.title = title;
        this.imageString = imageString;
        this.listingId = listingId;
        this.additionalInfo = additionalInfo;
    }

}
