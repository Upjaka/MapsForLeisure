package com.example.osmexample.presenter;

import androidx.annotation.NonNull;

import com.example.osmexample.model.MiniMarkerInfo;
import com.example.osmexample.model.MiniRouteInfo;
import com.example.osmexample.model.ObjectType;
import com.yandex.mapkit.map.PolylineMapObject;
import java.time.LocalDateTime;

public class TrackInfo {
    private final PolylineMapObject polyline;
    private String name;
    private String description;
    private ObjectType trackType;
    private final LocalDateTime dateTime;


    public TrackInfo(PolylineMapObject polyline, String name, String description, ObjectType trackType, LocalDateTime dateTime) {
        this.polyline = polyline;
        this.name = name;
        this.description = description;
        this.trackType = trackType;
        this.dateTime = dateTime;
    }

    public ObjectType getTrackType() {
        return trackType;
    }

    public void setTrackType(ObjectType trackType) {
        this.trackType = trackType;
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

    public PolylineMapObject getPolyline() {
        return polyline;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public MiniRouteInfo getStorageFormat() {
        return new MiniRouteInfo(polyline.getGeometry().getPoints(), name, description, trackType, dateTime);
    }

    @NonNull
    @Override
    public String toString() {
        return this.name + " " + polyline.toString();
    }

    @Override
    public int hashCode() {
        return polyline.hashCode();
    }
}
