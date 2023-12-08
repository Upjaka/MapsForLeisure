package com.example.osmexample.presenter;

import com.example.osmexample.model.MiniMarkerInfo;
import com.example.osmexample.model.MiniRouteInfo;
import com.example.osmexample.model.Model;
import com.yandex.mapkit.map.MapObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Presenter {
    private final Model model;
    private final String filesDir;
    private final Map<MapObject, MarkerInfo> markerInfoMap;
    private final Map<MapObject, RouteInfo> routeInfoMap;
    private final Map<MapObject, TrackInfo> trackInfoMap;

    public Presenter(String filesDir) {
        this.model = new Model();
        this.filesDir = filesDir;
        this.markerInfoMap = new HashMap<>();
        this.routeInfoMap = new HashMap<>();
        this.trackInfoMap = new HashMap<>();
        model.loadDataFromFiles(filesDir);
    }

    public void saveData() {
        model.saveDataToFiles(filesDir);
    }
}
