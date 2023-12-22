package com.example.osmexample.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Model {
    private final String filesDir;
    private final Set<MarkerInfo> markerList;
    private final Set<RouteInfo> routeList;
    private final Set<RouteInfo> trackList;
    private final List<User> users;

    public Model(String filesDir) {
        this.filesDir = filesDir;
        markerList = new HashSet<>();
        routeList = new HashSet<>();
        trackList = new HashSet<>();
        users = new ArrayList<>();
    }

    public List<MarkerInfo> getMarkerList() {
        return new ArrayList<>(markerList);
    }

    public List<RouteInfo> getRouteList() {
        return new ArrayList<>(routeList);
    }

    public List<RouteInfo> getTrackList() {
        return new ArrayList<>(trackList);
    }

    public void addMarker(MarkerInfo markerInfo) {
        markerList.add(markerInfo);
    }

    public void removeMarker(MarkerInfo markerInfo) {
        markerList.remove(markerInfo);
    }

    public void addRoute(RouteInfo routeInfo) {
        routeList.add(routeInfo);
    }

    public void removeRoute(RouteInfo routeInfo) {
        routeList.remove(routeInfo);
    }

    public void addTrack(RouteInfo trackInfo) {
        trackList.add(trackInfo);
    }

    public void removeTrack(RouteInfo trackInfo) {
        trackList.remove(trackInfo);
    }

    public void saveDataToFiles() {
        FileManager.saveMarkerListToFile(markerList, filesDir);
        FileManager.saveRouteListToFile(routeList, filesDir);
        FileManager.saveTracksToFile(trackList, filesDir);
    }

    public void loadDataFromFiles() {
        markerList.addAll(FileManager.loadMarkersFromFile(filesDir));
        routeList.addAll(FileManager.loadRoutesFromFile(filesDir));
        trackList.addAll(FileManager.loadTracksFromFile(filesDir));
    }
}
