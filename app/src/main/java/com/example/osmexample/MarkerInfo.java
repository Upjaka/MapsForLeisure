package com.example.osmexample;

import androidx.annotation.NonNull;

import com.yandex.mapkit.map.PlacemarkMapObject;

public class MarkerInfo {
    private PlacemarkMapObject placemark;
    private String name;
    private String description;
    private MarkerType markerType;


    public MarkerInfo(PlacemarkMapObject placemark, String name, String description, MarkerType markerType) {
        this.placemark = placemark;
        this.name = name;
        this.description = description;
        this.markerType = markerType;
    }

    public MarkerType getMarkerType() {
        return markerType;
    }

    public void setMarkerType(MarkerType markerType) {
        this.markerType = markerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PlacemarkMapObject getPlacemark() {
        return placemark;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name + " " + placemark.toString();
    }

    @Override
    public int hashCode() {
        return placemark.hashCode();
    }
}

