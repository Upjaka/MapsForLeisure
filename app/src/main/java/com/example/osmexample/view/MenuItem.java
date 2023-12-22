package com.example.osmexample.view;

public class MenuItem {
    private final int imageId;
    private final String text;

    public MenuItem(int imageId, String text) {
        this.imageId = imageId;
        this.text = text;
    }

    public int getImageId() {
        return imageId;
    }

    public String getText() {
        return text;
    }
}
