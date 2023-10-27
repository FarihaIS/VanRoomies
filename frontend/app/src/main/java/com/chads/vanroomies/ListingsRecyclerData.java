package com.chads.vanroomies;
// Reference: https://www.geeksforgeeks.org/recyclerview-using-gridlayoutmanager-in-android-with-example/
public class ListingsRecyclerData {
    private String title;
    private int imgId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public ListingsRecyclerData(String title, int imgId) {
        this.title = title;
        this.imgId = imgId;
    }

}
