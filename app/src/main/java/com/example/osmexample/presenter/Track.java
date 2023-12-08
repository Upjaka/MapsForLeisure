package com.example.osmexample.presenter;

import androidx.annotation.NonNull;

import com.example.osmexample.model.RouteInfo;
import com.example.osmexample.model.ObjectType;
import com.yandex.mapkit.map.PolylineMapObject;
import java.time.LocalDateTime;

public class Track {
    private final PolylineMapObject polyline;
    private final RouteInfo trackInfo;


    public Track(PolylineMapObject polyline, RouteInfo trackInfo) {
        this.polyline = polyline;
        this.trackInfo = trackInfo;
    }

    public ObjectType getRouteType() {
        return trackInfo.getRouteType();
    }

    public void setRouteType(ObjectType routeType) {
        trackInfo.setRouteType(routeType);
    }

    public String getName() {
        return trackInfo.getName();
    }

    public void setName(String name) {
        trackInfo.setName(name);
    }

    public String getDescription() {
        return trackInfo.getDescription();
    }

    public void setDescription(String description) {
        trackInfo.setDescription(description);
    }

    public PolylineMapObject getPolyline() {
        return polyline;
    }

    public LocalDateTime getDateTime() {
        return trackInfo.getDateTime();
    }

    public RouteInfo getTrackInfo() {
        return trackInfo;
    }

    @NonNull
    @Override
    public String toString() {
        return this.trackInfo.getName() + " " + polyline.getGeometry().getPoints();
    }

    @Override
    public int hashCode() {
        return polyline.hashCode();
    }
}
