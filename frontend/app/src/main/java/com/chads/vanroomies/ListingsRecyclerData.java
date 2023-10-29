package com.chads.vanroomies;
// Reference: https://www.geeksforgeeks.org/recyclerview-using-gridlayoutmanager-in-android-with-example/
public class ListingsRecyclerData {
    private String title;
    private String imageString;

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

    public ListingsRecyclerData(String title, String imageString) {
        this.title = title;
        this.imageString = imageString;
    }

}
