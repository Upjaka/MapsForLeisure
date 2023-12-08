package com.example.osmexample.presenter;

import androidx.annotation.NonNull;

import com.example.osmexample.model.MarkerInfo;
import com.example.osmexample.model.ObjectType;
import com.yandex.mapkit.map.PlacemarkMapObject;

import java.time.LocalDateTime;

public class Marker {
    private final PlacemarkMapObject placemark;
    private final MarkerInfo markerInfo;

    public Marker(PlacemarkMapObject placemark, MarkerInfo markerInfo) {
        this.placemark = placemark;
        this.markerInfo = markerInfo;
    }

    public ObjectType getMarkerType() {
        return markerInfo.getMarkerType();
    }

    public void setMarkerType(ObjectType markerType) {
        this.markerInfo.setMarkerType(markerType);
    }

    public String getName() {
        return markerInfo.getName();
    }

    public void setName(String name) {
        this.markerInfo.setName(name);
    }

    public String getDescription() {
        return markerInfo.getDescription();
    }

    public void setDescription(String description) {
        this.markerInfo.setDescription(description);
    }

    public PlacemarkMapObject getPlacemark() {
        return placemark;
    }

    public LocalDateTime getDateTime() {
        return markerInfo.getDateTime();
    }

    public MarkerInfo getMarkerInfo() {
        return this.markerInfo;
    }

    @NonNull
    @Override
    public String toString() {
        return this.markerInfo.getName() + " " + placemark.toString();
    }

    @Override
    public int hashCode() {
        return placemark.hashCode();
    }
}

