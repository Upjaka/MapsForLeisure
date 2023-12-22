package com.example.osmexample.model;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileManager {

    public static void saveMarkerListToFile(Set<MarkerInfo> markers, String filesDir) {
        Gson gson = new Gson();
        new File(filesDir + "/markers.json").delete();

        String json = gson.toJson(markers);
        try (FileWriter writer = new FileWriter(filesDir + "/markers.json")) {
            writer.write(json);
            Log.d("JSON", markers.size() + " markers are saved");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("JSON", "Error saving markers");
        }
    }

    public static List<MarkerInfo> loadMarkersFromFile(String filesDir) {
        //new File(filesDir + "/markers.json").delete();
        Gson gson = new Gson();
        ArrayList<MarkerInfo> markers;
        try (FileReader reader = new FileReader(filesDir + "/markers.json")) {
            Type listType = new TypeToken<ArrayList<MarkerInfo>>() {
            }.getType();
            markers = gson.fromJson(reader, listType);

        } catch (IOException e) {
            markers = new ArrayList<>();
        }
        String msg = markers.size() + " markers were downloaded from file";
        Log.d("JSON", msg);
        return markers;
    }

    public static void saveRouteListToFile(Set<RouteInfo> routes, String filesDir) {
        Gson gson = new Gson();
        new File(filesDir + "/routes.json").delete();

        String json = gson.toJson(routes);
        try (FileWriter writer = new FileWriter(filesDir + "/routes.json")) {
            writer.write(json);
            Log.d("JSON", routes.size() + " routes are saved");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("JSON", "Error saving routes");
        }
    }

    public static List<RouteInfo> loadRoutesFromFile(String filesDir) {
        //new File(getFilesDir() + "/routes.json").delete();
        Gson gson = new Gson();
        ArrayList<RouteInfo> routes;
        try (FileReader reader = new FileReader(filesDir + "/routes.json")) {
            Type listType = new TypeToken<ArrayList<RouteInfo>>() {
            }.getType();
            routes = gson.fromJson(reader, listType);

        } catch (IOException e) {
            routes = new ArrayList<>();
        }
        String msg = routes.size() + " routes were downloaded from file";
        Log.d("JSON", msg);
        return routes;
    }

    public static void saveTracksToFile(Set<RouteInfo> tracks, String filesDir) {
        Gson gson = new Gson();
        new File(filesDir + "/tracks.json").delete();

        String json = gson.toJson(tracks);
        try (FileWriter writer = new FileWriter(filesDir + "/tracks.json")) {
            writer.write(json);
            Log.d("JSON", tracks.size() + " tracks are saved");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("JSON", "Error saving tracks");
        }
    }

    public static List<RouteInfo> loadTracksFromFile(String filesDir) {
        //new File(getFilesDir() + "/tracks.json").delete();
        Gson gson = new Gson();
        ArrayList<RouteInfo> tracks;
        try (FileReader reader = new FileReader(filesDir + "/trracks.json")) {
            Type listType = new TypeToken<ArrayList<RouteInfo>>() {
            }.getType();
            tracks = gson.fromJson(reader, listType);

        } catch (IOException e) {
            tracks = new ArrayList<>();
        }
        String msg = tracks.size() + " tracks were downloaded from file";
        Log.d("JSON", msg);
        return tracks;
    }
}
