package com.example.osmexample.model;

import android.util.Log;

import com.example.osmexample.presenter.MarkerInfo;
import com.example.osmexample.presenter.RouteInfo;
import com.google.gson.reflect.TypeToken;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.MapObject;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileManager {

    public static void saveMarkerListToFile(Map<MapObject, MarkerInfo> markerInfoMap, String filesDir) {
        Gson gson = new Gson();
        new File(filesDir + "/markers.json").delete();
        ArrayList<MiniMarkerInfo> markers = new ArrayList<>();

        for (MarkerInfo markerInfo: markerInfoMap.values()) {
            double latitude = markerInfo.getPlacemark().getGeometry().getLatitude();
            double longitude = markerInfo.getPlacemark().getGeometry().getLongitude();
            markers.add(new MiniMarkerInfo(markerInfo.getPlacemark().getGeometry(), markerInfo.getName(), markerInfo.getDescription(), markerInfo.getMarkerType(), markerInfo.getDateTime()));
        }

        String json = gson.toJson(markers);
        try (FileWriter writer = new FileWriter(filesDir + "/markers.json")) {
            writer.write(json);
            Log.d("JSON", markers.size() + " markers are saved");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("JSON", "Error saving markers");
        }
    }

    public static List<MiniMarkerInfo> loadMarkersFromFile(String filesDir) {
        //new File(filesDir + "/markers.json").delete();
        Gson gson = new Gson();
        ArrayList<MiniMarkerInfo> markers;
        try (FileReader reader = new FileReader(filesDir + "/markers.json")) {
            Type listType = new TypeToken<ArrayList<MiniMarkerInfo>>(){}.getType();
            markers = gson.fromJson(reader, listType);

        } catch (IOException e) {
            markers = new ArrayList<>();
        }
        String msg = markers.size() + " markers were downloaded from file";
        Log.d("JSON", msg);
        return markers;
    }

    public static void saveRouteListToFile(Map<MapObject, RouteInfo> routeInfoMap, String filesDir) {
        Gson gson = new Gson();
        new File(filesDir + "/routes.json").delete();
        ArrayList<MiniRouteInfo> routes = new ArrayList<>();

        for (RouteInfo routeInfo: routeInfoMap.values()) {
            List<Point> points = routeInfo.getPolyline().getGeometry().getPoints();
            routes.add(new MiniRouteInfo(points, routeInfo.getName(), routeInfo.getDescription(), routeInfo.getRouteType(), routeInfo.getDateTime()));
        }

        String json = gson.toJson(routes);
        try (FileWriter writer = new FileWriter(filesDir + "/routes.json")) {
            writer.write(json);
            Log.d("JSON", routes.size() + " routes are saved");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("JSON", "Error saving routes");
        }
    }

    public static List<MiniRouteInfo> loadRoutesFromFile(String filesDir) {
        //new File(getFilesDir() + "/routes.json").delete();
        Gson gson = new Gson();
        ArrayList<MiniRouteInfo> routes;
        try (FileReader reader = new FileReader(filesDir + "/routes.json")) {
            Type listType = new TypeToken<ArrayList<MiniRouteInfo>>(){}.getType();
            routes = gson.fromJson(reader, listType);

        } catch (IOException e) {
            routes = new ArrayList<>();
        }
        String msg = routes.size() + " routes were downloaded from file";
        Log.d("JSON", msg);
        return routes;
    }

    public static void saveTracksToFile(Map<MapObject, RouteInfo> trackInfoMap, String filesDir) {
        Gson gson = new Gson();
        new File(filesDir + "/tracks.json").delete();
        ArrayList<MiniRouteInfo> tracks = new ArrayList<>();

        for (RouteInfo routeInfo: trackInfoMap.values()) {
            List<Point> points = routeInfo.getPolyline().getGeometry().getPoints();
            tracks.add(new MiniRouteInfo(points, routeInfo.getName(), routeInfo.getDescription(), routeInfo.getRouteType(), routeInfo.getDateTime()));
        }

        String json = gson.toJson(tracks);
        try (FileWriter writer = new FileWriter(filesDir + "/tracks.json")) {
            writer.write(json);
            Log.d("JSON", tracks.size() + " tracks are saved");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("JSON", "Error saving tracks");
        }
    }

    public static List<MiniRouteInfo> loadTracksFromFile(String filesDir) {
        //new File(getFilesDir() + "/tracks.json").delete();
        Gson gson = new Gson();
        ArrayList<MiniRouteInfo> tracks;
        try (FileReader reader = new FileReader(filesDir + "/trracks.json")) {
            Type listType = new TypeToken<ArrayList<MiniRouteInfo>>(){}.getType();
            tracks = gson.fromJson(reader, listType);

        } catch (IOException e) {
            tracks = new ArrayList<>();
        }
        String msg = tracks.size() + " tracks were downloaded from file";
        Log.d("JSON", msg);
        return tracks;
    }
}
