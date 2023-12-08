package com.example.osmexample.presenter;

import androidx.annotation.NonNull;

import com.example.osmexample.model.ObjectType;
import com.yandex.mapkit.map.PolylineMapObject;
import java.time.LocalDateTime;

public class RouteInfo {
    private final PolylineMapObject polyline;
    private String name;
    private String description;
    private ObjectType routeType;
    private final LocalDateTime dateTime;


    public RouteInfo(PolylineMapObject polyline, String name, String description, ObjectType routeType, LocalDateTime dateTime) {
        this.polyline = polyline;
        this.name = name;
        this.description = description;
        this.routeType = routeType;
        this.dateTime = dateTime;
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

    public PolylineMapObject getPolyline() {
        return polyline;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
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
