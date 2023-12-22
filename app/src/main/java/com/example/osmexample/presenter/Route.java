package com.example.osmexample.presenter;

import androidx.annotation.NonNull;

import com.example.osmexample.model.RouteInfo;
import com.example.osmexample.model.ObjectType;
import com.yandex.mapkit.map.PolylineMapObject;
import java.time.LocalDateTime;

public class Route {
    private final PolylineMapObject polyline;
    private final RouteInfo routeInfo;


    public Route(PolylineMapObject polyline, RouteInfo routeInfo) {
        this.polyline = polyline;
        this.routeInfo = routeInfo;
    }

    public ObjectType getRouteType() {
        return routeInfo.getRouteType();
    }

    public void setRouteType(ObjectType routeType) {
        routeInfo.setRouteType(routeType);
    }

    public String getName() {
        return routeInfo.getName();
    }

    public void setName(String name) {
        routeInfo.setName(name);
    }

    public String getDescription() {
        return routeInfo.getDescription();
    }

    public void setDescription(String description) {
        routeInfo.setDescription(description);
    }

    public boolean isVisible() {
        return routeInfo.isVisible();
    }

    public void setVisible(boolean visible) {
        routeInfo.setVisible(visible);
    }

    public PolylineMapObject getPolyline() {
        return polyline;
    }

    public LocalDateTime getDateTime() {
        return routeInfo.getDateTime();
    }

    public RouteInfo getRouteInfo() {
        return routeInfo;
    }

    @NonNull
    @Override
    public String toString() {
        return this.routeInfo.getName() + " " + polyline.getGeometry().getPoints();
    }

    @Override
    public int hashCode() {
        return polyline.hashCode();
    }
}
