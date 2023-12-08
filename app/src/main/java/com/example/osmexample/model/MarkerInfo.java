package com.example.osmexample.model;

import com.yandex.mapkit.geometry.Point;

import java.time.LocalDateTime;

public class MarkerInfo {
    private final Point point;
    private String name;
    private String description;
    private ObjectType markerType;
    private final String dateTime;
    private boolean visible;

    public MarkerInfo(Point point, String name, String description, ObjectType markerType, LocalDateTime dateTime, boolean visible) {
        this.point = point;
        this.name = name;
        this.description = description;
        this.markerType = markerType;
        this.dateTime = dateTime.toString();
        this.visible = visible;
    }

    public Point getPoint() {
        return point;
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

    public LocalDateTime getDateTime() {
        return LocalDateTime.parse(dateTime);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
