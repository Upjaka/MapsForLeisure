package com.example.osmexample.model;

import com.yandex.mapkit.geometry.Point;

import java.time.LocalDateTime;
import java.util.List;

public class RouteInfo {
    private final List<Point> points;
    private String name;
    private String description;
    private ObjectType routeType;
    private final LocalDateTime dateTime;
    private boolean visible;


    public RouteInfo(List<Point> points, String name, String description, ObjectType routeType, LocalDateTime dateTime, boolean visible) {
        this.points = points;
        this.name = name;
        this.description = description;
        this.routeType = routeType;
        this.dateTime = dateTime;
        this.visible = visible;
    }

    public List<Point> getPoints() {
        return points;
    }

    public ObjectType getRouteType() {
        return routeType;
    }

    public void setRouteType(ObjectType routeType) {
        this.routeType = routeType;
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
        return dateTime;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
