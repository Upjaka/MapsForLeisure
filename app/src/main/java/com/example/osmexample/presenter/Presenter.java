package com.example.osmexample.presenter;

import com.example.osmexample.model.MarkerInfo;
import com.example.osmexample.model.Model;
import com.example.osmexample.model.ObjectType;
import com.example.osmexample.model.RouteInfo;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolylineMapObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Presenter {
    private final Model model;
    private final Map<MapObject, Marker> markerMap;
    private final Map<MapObject, Route> routeMap;
    private final Map<MapObject, Route> trackMap;

    public Presenter(String filesDir) {
        this.model = new Model(filesDir);
        this.markerMap = new HashMap<>();
        this.routeMap = new HashMap<>();
        this.trackMap = new HashMap<>();
        model.loadDataFromFiles();
    }

    public void saveData() {
        model.saveDataToFiles();
    }

    public List<MarkerInfo> getMarkerList() {
        return model.getMarkerList();
    }

    public List<RouteInfo> getRouteList() {
        return model.getRouteList();
    }

    public List<RouteInfo> getTrackList() {
        return model.getTrackList();
    }

    public List<Marker> getMarkersWithType(ObjectType type) {
        List<Marker> result = new ArrayList<>();
        for (Marker marker : markerMap.values()) {
            if (marker.getMarkerType() == type) {
                result.add(marker);
            }
        }
        return result;
    }

    public List<Route> getRoutesWithType(ObjectType type) {
        List<Route> result = new ArrayList<>();
        for (Route route : routeMap.values()) {
            if (route.getRouteType() == type) {
                result.add(route);
            }
        }
        return result;
    }

    public List<Route> getTracksWithType(ObjectType type) {
        List<Route> result = new ArrayList<>();
        for (Route track : trackMap.values()) {
            if (track.getRouteType() == type) {
                result.add(track);
            }
        }
        return result;
    }

    public void addMarker(Marker marker) {
        markerMap.put(marker.getPlacemark(), marker);
        model.addMarker(marker.getMarkerInfo());
    }

    public void addMarker(PlacemarkMapObject placemark, String name, String description, ObjectType type, LocalDateTime dateTime) {
        MarkerInfo markerInfo = new MarkerInfo(placemark.getGeometry(), name, description, type, dateTime, true);
        Marker marker = new Marker(placemark, markerInfo);
        addMarker(marker);
    }

    public void addRoute(Route route) {
        routeMap.put(route.getPolyline(), route);
        model.addRoute(route.getRouteInfo());
    }

    public void addRoute(PolylineMapObject polyline, String name, String description, ObjectType type, LocalDateTime dateTime) {
        RouteInfo routeInfo = new RouteInfo(polyline.getGeometry().getPoints(), name, description, type, dateTime, true);
        Route route = new Route(polyline, routeInfo);
        addRoute(route);
    }

    public void addTrack(Route track) {
        trackMap.put(track.getPolyline(), track);
        model.addTrack(track.getRouteInfo());
    }

    public void addTrack(PolylineMapObject polyline, String name, String description, ObjectType type, LocalDateTime dateTime) {
        RouteInfo trackInfo = new RouteInfo(polyline.getGeometry().getPoints(), name, description, type, dateTime, true);
        Route track = new Route(polyline, trackInfo);
        addTrack(track);
    }

    public Marker getMarker(MapObject placemark) {
        return markerMap.get(placemark);
    }

    public Marker getMarker(int index) {
        Point point = model.getMarkerList().get(index).getPoint();
        for (MapObject mapObject : markerMap.keySet()) {
            PlacemarkMapObject placemark = (PlacemarkMapObject) mapObject;
            if (placemark.getGeometry().getLongitude() == point.getLongitude()
                    && placemark.getGeometry().getLatitude() == point.getLatitude()) {
                return markerMap.get(mapObject);
            }
        }
        return null;
    }

    public Route getRoute(MapObject polyline) {
        return routeMap.get(polyline);
    }

    public Route getRoute(int index) {
        RouteInfo routeInfo = model.getRouteList().get(index);

        for (Route route : routeMap.values()) {
            if (route.getRouteInfo() == routeInfo) {
                return route;
            }
        }
        return null;
    }

    public Route getTrack(MapObject polyline) {
        return trackMap.get(polyline);
    }

    public Route getTrack(int index) {
        RouteInfo trackInfo = model.getTrackList().get(index);

        for (Route track : trackMap.values()) {
            if (track.getRouteInfo() == trackInfo) {
                return track;
            }
        }
        return null;
    }

    public void removeMarker(MapObject placemark) {
        model.removeMarker(markerMap.get(placemark).getMarkerInfo());
        markerMap.remove(placemark);
    }


    public void removeRoute(MapObject polyline) {
        model.removeRoute(routeMap.get(polyline).getRouteInfo());
        routeMap.remove(polyline);
    }

    public void removeTrack(MapObject polyline) {
        model.removeTrack(trackMap.get(polyline).getRouteInfo());
        trackMap.remove(polyline);
    }
}