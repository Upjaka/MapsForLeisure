package com.example.osmexample;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.Manifest;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.location.LocationRequest;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.views.overlay.MapEventsOverlay;

import androidx.core.app.ActivityCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Activity implements MapEventsReceiver {
    MapView map = null;
    GeoPoint userLocation = null;
    Gson gson = new Gson();
    ArrayList<Double[]> markers = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        //inflate and create the map
        setContentView(R.layout.activity_main);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setScrollableAreaLimitLatitude(
                MapView.getTileSystem().getMaxLatitude(), MapView.getTileSystem().getMinLatitude(), 0
        );

        map.setMinZoomLevel(3.0);
        map.getController().setZoom(15);
        map.setHorizontalMapRepetitionEnabled(true);
        map.setVerticalMapRepetitionEnabled(false);

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
        map.getOverlays().add(0, mapEventsOverlay);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Создайте запрос местоположения
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Проверьте разрешения
        if (checkLocationPermission()) {
            // Разрешения на местоположение уже предоставлены

            // Запросите текущее местоположение
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // Ваш код для обработки полученного местоположения
                        userLocation = new GeoPoint(latitude, longitude);

                        // Добавление маркера текущего местоположения
                        addMarker(userLocation, false);
                    } else {
                        // Местоположение не доступно, обработайте это соответственно
                        userLocation = new GeoPoint(58.200897, 68.253976);
                    }
                    // Центрируйте карту на местоположении пользователя
                    map.getController().setCenter(userLocation);
                }
            });
        } else {
            // Запрос разрешений
            requestLocationPermission();
        }

        // Добавление меток на карту
        try (FileReader reader = new FileReader(getFilesDir() + "/markers.json")) {
            Type listType = new TypeToken<ArrayList<Double[]>>(){}.getType();
            markers = gson.fromJson(reader, listType);

        } catch (IOException e) {
            markers = new ArrayList<>();
        }
        String msg = "Data downloaded from file: ";
        for (Double[] point: markers) {
            addMarker(new GeoPoint(point[0], point[1]), false);
            msg = msg + Arrays.toString(point);
        }
        Log.d("JSON", msg);
    }

    public void onResume() {
        super.onResume();
        map.onResume();
    }

    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Сохранение JSON в файл
        String json = gson.toJson(markers);
        try (FileWriter writer = new FileWriter(getFilesDir() + "/markers.json")) {
            writer.write(json);
            String msg = "Data saved to file: ";
            for (Double[] point: markers) {
                msg = msg + Arrays.toString(point);
            }
            Log.d("JSON", msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Проверка наличия разрешений на доступ к местоположению
    private boolean checkLocationPermission() {
        int fineLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED;
    }

    // Запрос разрешений на доступ к местоположению
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 12345);
    }

    private void addMarker(GeoPoint markerLocation, boolean addToArray) {
        Double[] point = {markerLocation.getLatitude(), markerLocation.getLongitude()};
        if (addToArray) markers.add(point);
        Marker startMarker = new Marker(map);
        startMarker.setPosition(markerLocation);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//        startMarker.setIcon(ResourcesCompat.getDrawable(getResources(),
//                R.drawable.marker, getApplicationContext().getTheme()));
        map.getOverlays().add(startMarker);
        map.invalidate();
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        addMarker(p, true);
//        RoadManager roadManager = new OSRMRoadManager(this, "MY_USER_AGENT");
//        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
//        waypoints.add(userLocation);
//        waypoints.add(p);
//        Road road = roadManager.getRoad(waypoints);
//        if (road.mStatus != Road.STATUS_OK)
//            Toast.makeText(this, "Error when loading the road - status=" + road.mStatus, Toast.LENGTH_SHORT).show();
//
//        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
//        map.getOverlays().add(roadOverlay);
        return false;
    }

    public void onMapCenterButtonClick(View view) {
        map.getController().setCenter(userLocation);
    }

    //    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case 12345:
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission granted
//                    centerMapOnLocation();
//                } else {
//                    // permission denied
//                    map.getController().setCenter(new GeoPoint(58.200897, 68.253976));
//                }
//                return;
//        }
//    }
}
