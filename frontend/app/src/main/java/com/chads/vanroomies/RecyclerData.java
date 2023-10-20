package com.chads.vanroomies;
// Reference: https://www.geeksforgeeks.org/recyclerview-using-gridlayoutmanager-in-android-with-example/
public class RecyclerData {
    private String title;
    private int imgid;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImgid() {
        return imgid;
    }

    public void setImgid(int imgid) {
        this.imgid = imgid;
    }

    public RecyclerData(String title, int imgid) {
        this.title = title;
        this.imgid = imgid;
    }

}
