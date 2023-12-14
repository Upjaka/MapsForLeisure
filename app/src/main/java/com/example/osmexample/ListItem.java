package com.example.osmexample;

public class ListItem {
    private final int imageId1;
    private final String text1;
    private final String text2;
    private final String dateTime;
    private final int imageId2;
    private final int imageId3;

    public ListItem(int imageId1, String text1, String text2, String dateTime, int imageId2, int imageId3) {
        this.imageId1 = imageId1;
        this.text1 = text1;
        this.text2 = text2;
        this.dateTime = dateTime;
        this.imageId2 = imageId2;
        this.imageId3 = imageId3;
    }

    public int getImageId1() {
        return imageId1;
    }
    public String getText1() {
        return text1;
    }
    public String getText2() {return text2;}
    public String getDateTime() {return dateTime;}
    public int getImageId2() {
        return imageId2;
    }
    public int getImageId3() {
        return imageId3;
    }
}
