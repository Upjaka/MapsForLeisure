package com.example.osmexample.model;

import com.yandex.mapkit.geometry.Point;

import java.time.LocalDateTime;
import java.util.List;

public class MiniRouteInfo {
    private List<Point> points;
    private final String name;
    private final String description;
    private final ObjectType markerType;
    private final LocalDateTime dateTime;


    public MiniRouteInfo(List<Point> points, String name, String description, ObjectType markerType, LocalDateTime dateTime) {
        this.points = points;
        this.name = name;
        this.description = description;
        this.markerType = markerType;
        this.dateTime = dateTime;
    }

    public List<Point> getPoints() {
        return points;
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
