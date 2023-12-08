package com.example.osmexample.presenter;

import androidx.annotation.NonNull;

import com.example.osmexample.model.MiniMarkerInfo;
import com.example.osmexample.model.ObjectType;
import com.yandex.mapkit.map.PlacemarkMapObject;

import java.time.LocalDateTime;

public class MarkerInfo {
    private final PlacemarkMapObject placemark;
    private String name;
    private String description;
    private ObjectType markerType;
    private final LocalDateTime dateTime;


    public MarkerInfo(PlacemarkMapObject placemark, String name, String description, ObjectType markerType, LocalDateTime dateTime) {
        this.placemark = placemark;
        this.name = name;
        this.description = description;
        this.markerType = markerType;
        this.dateTime = dateTime;
    }

    public ObjectType getMarkerType() {
        return markerType;
    }

    public void setMarkerType(ObjectType markerType) {
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

    public MiniMarkerInfo getStorageFormat() {
        return new MiniMarkerInfo(placemark.getGeometry(), name, description, markerType, dateTime);
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

