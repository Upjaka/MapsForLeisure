package com.example.osmexample.presenter;

import com.example.osmexample.model.MarkerInfo;
import com.example.osmexample.model.Model;
import com.example.osmexample.model.ObjectType;
import com.example.osmexample.model.RouteInfo;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.PlacemarkMapObject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Presenter {
    private final Model model;
    private final Map<MapObject, Marker> markerInfoMap;
    private final Map<MapObject, Route> routeInfoMap;
    private final Map<MapObject, Track> trackInfoMap;

    public Presenter(String filesDir) {
        this.model = new Model(filesDir);
        this.markerInfoMap = new HashMap<>();
        this.routeInfoMap = new HashMap<>();
        this.trackInfoMap = new HashMap<>();
        model.loadDataFromFiles();
    }

    public void saveData() {
        model.saveDataToFiles();
    }

    public void loadData() {
        model.loadDataFromFiles();
    }

    public List<MarkerInfo> getMarkerList() {
        return model.getMarkerList();
    }

    public void addMarker(Marker marker) {
        markerInfoMap.put(marker.getPlacemark(), marker);
        model.addMarker(marker.getMarkerInfo());
    }

    public void addMarker(PlacemarkMapObject placemark, String name, String description, ObjectType type, LocalDateTime dateTime) {
        MarkerInfo markerInfo = new MarkerInfo(placemark.getGeometry(), name, description, type, dateTime, true);
        Marker marker = new Marker(placemark, markerInfo);
        addMarker(marker);
    }

    public void addRoute(Route route) {
        routeInfoMap.put(route.getPolyline(), route);
        model.addRoute(route.getRouteInfo());
    }

    public void addTrack(Track track) {
        trackInfoMap.put(track.getPolyline(), track);
        model.addTrack(track.getTrackInfo());
    }

    public Marker getMarker(MapObject placemark) {
        return markerInfoMap.get(placemark);
    }

    public Route getRoute(MapObject polyline) {
        return routeInfoMap.get(polyline);
    }

    public Track getTrack(MapObject polyline) {
        return trackInfoMap.get(polyline);
    }

    public void removeMarker(MapObject placemark) {
        markerInfoMap.remove(placemark);
        model.removeMarker(markerInfoMap.get(placemark).getMarkerInfo());
    }

    public void removeRoute(MapObject polyline) {
        routeInfoMap.remove(polyline);
        model.removeRoute(routeInfoMap.get(polyline).getRouteInfo());
    }

    public void removeTrack(MapObject polyline) {
        trackInfoMap.remove(polyline);
        model.removeTrack(trackInfoMap.get(polyline).getTrackInfo());
    }
}
