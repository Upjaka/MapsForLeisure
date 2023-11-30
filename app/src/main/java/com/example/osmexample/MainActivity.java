package com.example.osmexample;

import static com.yandex.mapkit.Animation.Type.SMOOTH;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.TextStyle;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.time.LocalDateTime;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity{
    private final String API_KEY = "1c1210f8-c152-4c8d-96ae-ac504e3662c4";
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private final float MARKER_SCALE = 0.06f;
    private final float LOCATION_MARKER_SCALE = 0.14f;
    private final float START_ZOOM = 14.0f;
    private MapView mapView = null;
    private Point userLocation = new Point(0, 0);
    private PlacemarkMapObject locationMarker = null;
    private InputListener inputListener = null;
    private FrameLayout mainLayout = null;
    private LinearLayout setMarkerLayout = null;
    private LinearLayout markerInfoPanel = null;
    private LocationManager locationManager;
    private MapObjectCollection mapObjects = null;
    private MapObject clickedMarker = null;
    private java.util.Map<MapObject, MarkerInfo> markerInfoMap = null;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(API_KEY);
        MapKitFactory.initialize(this);

        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();

        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapview);
        mainLayout = findViewById(R.id.MainLayout);
        setMarkerLayout = findViewById(R.id.setMarkerLayout);
        markerInfoPanel = findViewById(R.id.markerInfoPanel);


        // Определение местоположения
        requestLocationPermission();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastLocation != null) {
            userLocation = new Point(lastLocation.getLatitude(), lastLocation.getLongitude());
        }

        // Добавление маркера текущего местоположения
        markerInfoMap = new HashMap<>();
        mapObjects = mapView.getMap().getMapObjects().addCollection();
        ImageProvider imageProvider = ImageProvider.fromResource(this, R.drawable.star);
        locationMarker = mapObjects.addPlacemark();
        locationMarker.setGeometry(userLocation);
        locationMarker.setIcon(imageProvider);
        locationMarker.setIconStyle(new IconStyle().setScale(LOCATION_MARKER_SCALE));
        locationMarker.addTapListener(onMarkerTapListener);

        // Обработчик долгого нажатия на карту
        inputListener = new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {

            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
                Toast.makeText(context, "Передвиньте маркер на нужную позицию и нажмите на него", Toast.LENGTH_LONG).show();

                mapView.getMap().move(
                        new CameraPosition(point, map.getCameraPosition().getZoom(), 0.0f, 0.0f),
                        new Animation(SMOOTH, 0.3f),
                        null
                );

                ImageView centerMarker = findViewById(R.id.centerMarker);
                centerMarker.setVisibility(View.VISIBLE);
            }
        };

        mapView.getMap().addInputListener(inputListener);
        Log.d("TagOnCreate", "Finish initialization " + userLocation.getLatitude() + " " + userLocation.getLongitude());

        // Перемещение камеры на текущее местоположение
        mapView.getMap().move(
                new CameraPosition(userLocation, START_ZOOM, 0.0f, 0.0f),
                new Animation(SMOOTH, 0.3f),
                null
        );
    }

    // Изменение положения метки при изменении текущего местоположения
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            userLocation = new Point(location.getLatitude(), location.getLongitude());
            locationMarker.setGeometry(userLocation);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {}

        @Override
        public void onProviderEnabled(@NonNull String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    // Получение нового текущего местоположения
    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 2, 2, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 2, 2,
                locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    public void onMapCenterButtonClick(View view) {
        float currentZoom = mapView.getMap().getCameraPosition().getZoom();
        mapView.getMap().move(
                new CameraPosition(userLocation, currentZoom, 0.0f, 0.0f),
                new Animation(SMOOTH, 0.8f),
                null
        );
    }

    public void onPlusZoomButtonClick(View view) {
        Point currentPosition = mapView.getMap().getCameraPosition().getTarget();
        float currentZoom = mapView.getMap().getCameraPosition().getZoom();
        mapView.getMap().move(
                new CameraPosition(currentPosition, currentZoom + 1f, 0.0f, 0.0f),
                new Animation(SMOOTH, 0.3f),
                null
        );
    }

    public void onMinusZoomButtonClick(View view) {
        Point currentPosition = mapView.getMap().getCameraPosition().getTarget();
        float currentZoom = mapView.getMap().getCameraPosition().getZoom();
        mapView.getMap().move(
                new CameraPosition(currentPosition, currentZoom - 1f, 0.0f, 0.0f),
                new Animation(SMOOTH, 0.3f),
                null
        );
    }

    public void onCenterMarkerClick(View view) {
        mainLayout.setVisibility(View.INVISIBLE);
        findViewById(R.id.centerMarker).setVisibility(View.INVISIBLE);
        setMarkerLayout.setVisibility(View.VISIBLE);
    }

    public void onSaveMarkerButton(View view) {
        ScreenPoint screenPoint = new ScreenPoint(mapView.getMapWindow().width() / 2f,
                mapView.getMapWindow().height() / 2f);
        Point point = mapView.getMapWindow().screenToWorld(screenPoint);
        TextView nameView = findViewById(R.id.editMarkerName);
        String markerName = nameView.getText().toString();
        TextView descriptionView = findViewById(R.id.editTextDescription);
        String markerDescription = descriptionView.getText().toString();
        MarkerType markerType = MarkerType.DEFAULT;
        RadioButton mushroomRadioButton = findViewById(R.id.mushroomRadioButton);
        RadioButton fishRadioButton = findViewById(R.id.fishRadioButton);
        RadioButton walkRadioButton = findViewById(R.id.walkRadioButton);
        ImageProvider imageProvider = ImageProvider.fromResource(this, R.drawable.default_marker);
        if (mushroomRadioButton.isChecked()) {
            markerType = MarkerType.MUSHROOM;
            imageProvider = ImageProvider.fromResource(this, R.drawable.mushroom_marker);
            mushroomRadioButton.setChecked(false);
        } else if (fishRadioButton.isChecked()) {
            markerType = MarkerType.FISH;
            imageProvider = ImageProvider.fromResource(this, R.drawable.fish_marker);
            fishRadioButton.setChecked(false);
        } else if (walkRadioButton.isChecked()) {
            markerType = MarkerType.WALK;
            imageProvider = ImageProvider.fromResource(this, R.drawable.walk_marker);
            walkRadioButton.setChecked(false);
        }
        if (markerInfoPanel.getVisibility() == View.VISIBLE) {
            MarkerInfo markerInfo = markerInfoMap.get(clickedMarker);
            if (markerInfo != null) {
                markerInfo.setName(markerName);
                markerInfo.setDescription(markerDescription);
                markerInfo.setMarkerType(markerType);

                PlacemarkMapObject placemark = markerInfo.getPlacemark();
                placemark.setText(markerName);

                placemark.setIcon(imageProvider);
                placemark.setTextStyle(new TextStyle().setPlacement(TextStyle.Placement.TOP));
                placemark.setIconStyle(new IconStyle().setScale(MARKER_SCALE).setAnchor(new PointF(0.5f, 1.0f)));

                TextView textName = findViewById(R.id.markerName);
                textName.setText(markerInfo.getName());
                TextView textDescription = findViewById(R.id.markerDescription);
                textDescription.setText(markerInfo.getDescription());
            }
        }
        else {
            addMarker(point, markerName, markerDescription, markerType);
        }
        nameView.setText("");
        descriptionView.setText("");
        setMarkerLayout.setVisibility(View.INVISIBLE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    public void onCancelMarkerButton(View view) {
        setMarkerLayout.setVisibility(View.INVISIBLE);
        mainLayout.setVisibility(View.VISIBLE);
        RadioButton mushroomRadioButton = findViewById(R.id.mushroomRadioButton);
        RadioButton fishRadioButton = findViewById(R.id.fishRadioButton);
        RadioButton walkRadioButton = findViewById(R.id.walkRadioButton);
        mushroomRadioButton.setChecked(false);
        fishRadioButton.setChecked(false);
        walkRadioButton.setChecked(false);
    }

    public void onChangeMarkerButtonClicked(View view) {
        mainLayout.setVisibility(View.INVISIBLE);
        setMarkerLayout.setVisibility(View.VISIBLE);

        MarkerInfo markerInfo = markerInfoMap.get(clickedMarker);
        TextView nameView = findViewById(R.id.editMarkerName);
        TextView descriptionView = findViewById(R.id.editTextDescription);
        if (markerInfo == null) {
            nameView.setText("");
            descriptionView.setText("");
        }
        else {
            nameView.setText(markerInfo.getName());
            descriptionView.setText(markerInfo.getDescription());
        }
    }

    public void onDeleteMarkerButtonClicked(View view) {
        mapObjects.remove(clickedMarker);
        markerInfoMap.remove(clickedMarker);
        markerInfoPanel.setVisibility(View.INVISIBLE);
    }

    private void addMarker(Point position, String name, String description, MarkerType type) {
        ImageProvider imageProvider = ImageProvider.fromResource(this, R.drawable.default_marker);
        if (type == MarkerType.MUSHROOM) {
            imageProvider = ImageProvider.fromResource(this, R.drawable.mushroom_marker);
        } else if (type == MarkerType.FISH) {
            imageProvider = ImageProvider.fromResource(this, R.drawable.fish_marker);
        } else if (type == MarkerType.WALK) {
            imageProvider = ImageProvider.fromResource(this, R.drawable.walk_marker);
        }
        PlacemarkMapObject placemark = mapObjects.addPlacemark();
        placemark.setGeometry(position);
        placemark.setIcon(imageProvider);
        placemark.setText(name);
        placemark.setTextStyle(new TextStyle().setPlacement(TextStyle.Placement.TOP));
        placemark.setIconStyle(new IconStyle().setScale(MARKER_SCALE).setAnchor(new PointF(0.5f, 1.0f)));
        placemark.addTapListener(onMarkerTapListener);
        MarkerInfo markerInfo = new MarkerInfo(placemark, name, description, type, LocalDateTime.now());
        markerInfoMap.put(placemark, markerInfo);
    }

    private final MapObjectTapListener onMarkerTapListener = (mapObject, point) -> {
        float currentZoom = mapView.getMap().getCameraPosition().getZoom();
        mapView.getMap().move(
                new CameraPosition(point, currentZoom, 0.0f, 0.0f),
                new Animation(SMOOTH, 0.3f),
                null
        );
        if (mapObject != locationMarker) {
            clickedMarker = mapObject;
            markerInfoPanel.setVisibility(View.VISIBLE);
            MarkerInfo markerInfo = markerInfoMap.get(mapObject);
            if (markerInfo == null) {
                Toast.makeText(getApplicationContext(), "Маркер не найден", Toast.LENGTH_LONG).show();
            }
            else {
                TextView markerName = findViewById(R.id.markerName);
                markerName.setText(markerInfo.getName());
                TextView markerDescription = findViewById(R.id.markerDescription);
                markerDescription.setText(markerInfo.getDescription());
            }
        }
        return false;
    };

    public void onCloseMarkerInfoButtonClicked(View view) {
        markerInfoPanel.setVisibility(View.INVISIBLE);
    }

    private boolean checkLocationPermission() {
        int fineLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED;
    }

    // Запрос разрешений на доступ к местоположению
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }
}