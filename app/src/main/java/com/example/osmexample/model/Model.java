package com.example.osmexample.model;

import com.example.osmexample.presenter.MarkerInfo;
import com.example.osmexample.presenter.RouteInfo;
import com.example.osmexample.presenter.TrackInfo;
import com.yandex.mapkit.map.MapObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {
    private final List<MiniMarkerInfo> markerList;
    private final List<MiniRouteInfo> routeList;
    private final List<MiniRouteInfo> trackList;


    public Model() {
        markerList = new ArrayList<>();
        routeList = new ArrayList<>();
        trackList = new ArrayList<>();

    }

    public List<MiniMarkerInfo> getMarkerList() {
        return markerList;
    }

    public List<MiniRouteInfo> getRouteList() {
        return routeList;
    }

    public List<MiniRouteInfo> getTrackList() {
        return trackList;
    }

    public void saveDataToFiles(String filesDir) {
//        FileManager.saveMarkerListToFile(markerList, filesDir);
//        FileManager.saveRouteListToFile(routeList, filesDir);
//        FileManager.saveTracksToFile(trackList, filesDir);
    }

    public void loadDataFromFiles(String filesDir) {
        markerList.addAll(FileManager.loadMarkersFromFile(filesDir));
        routeList.addAll(FileManager.loadRoutesFromFile(filesDir));
        trackList.addAll(FileManager.loadTracksFromFile(filesDir));
    }
}
