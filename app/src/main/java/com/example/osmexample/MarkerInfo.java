package com.example.osmexample;

import androidx.annotation.NonNull;

import com.yandex.mapkit.map.PlacemarkMapObject;

import java.time.LocalDateTime;

public class MarkerInfo {
    private PlacemarkMapObject placemark;
    private String name;
    private String description;
    private MarkerType markerType;
    private LocalDateTime dateTime;


    public MarkerInfo(PlacemarkMapObject placemark, String name, String description, MarkerType markerType, LocalDateTime dateTime) {
        this.placemark = placemark;
        this.name = name;
        this.description = description;
        this.markerType = markerType;
        this.dateTime = dateTime;
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

    public LocalDateTime getDateTime() {
        return dateTime;
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

