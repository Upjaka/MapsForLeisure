package com.example.osmexample.model;

import com.yandex.mapkit.geometry.Point;

import java.time.LocalDateTime;

public class MiniMarkerInfo {
//    private final double latitude;
//    private final double longitude;
    private final Point point;
    private final String name;
    private final String description;
    private final ObjectType markerType;
    private final LocalDateTime dateTime;


    public MiniMarkerInfo(Point point, String name, String description, ObjectType markerType, LocalDateTime dateTime) {
//        this.latitude = latitude;
//        this.longitude = longitude;
        this.point = point;
        this.name = name;
        this.description = description;
        this.markerType = markerType;
        this.dateTime = dateTime;
    }

//    public double getLatitude() {
//        return latitude;
//    }
//
//    public double getLongitude() {
//        return longitude;
//    }

    public Point getPoint() {
        return point;
    }

    public ObjectType getMarkerType() {
        return markerType;
    }

    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }


    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
