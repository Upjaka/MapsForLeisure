package com.example.osmexample;

import static com.yandex.mapkit.Animation.Type.SMOOTH;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.map.TextStyle;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.masstransit.PedestrianRouter;
import com.yandex.mapkit.transport.masstransit.Route;
import com.yandex.mapkit.transport.masstransit.Session;
import com.yandex.mapkit.transport.masstransit.TimeOptions;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private final String API_KEY = "1c1210f8-c152-4c8d-96ae-ac504e3662c4";
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private final int LOADING_SCREEN_TIME_OUT = 1000;
    private final float MARKER_SCALE = 0.06f;
    private final float LOCATION_MARKER_SCALE = 0.14f;
    private final float START_ZOOM = 14.0f;
    private MapView mapView = null;
    private Point userLocation = new Point(0, 0);
    private PlacemarkMapObject locationMarker = null;
    private InputListener inputListener = null;
    private FrameLayout loadingScreen = null;
    private FrameLayout mainLayout = null;
    private LinearLayout setMarkerLayout = null;
    private LinearLayout markerInfoPanel = null;
    private ListView menu = null;
    private LocationManager locationManager;
    private MapObjectCollection mapObjects = null;
    private MapObject clickedMarker = null;
    private ImageView centerMarker = null;
    private java.util.Map<MapObject, MarkerInfo> markerInfoMap = null;
    private Gson gson = null;
    private boolean isTracking = false;
    private List<Point> track = null;
    private List<PolylineMapObject> trackPolylines = null;
    private List<PolylineMapObject> routePolylines = null;
    private PedestrianRouter pedestrianRouter = null;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(API_KEY);
        MapKitFactory.initialize(this);

        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();

        mapView = findViewById(R.id.mapview);
        mainLayout = findViewById(R.id.MainLayout);
        setMarkerLayout = findViewById(R.id.setMarkerLayout);
        markerInfoPanel = findViewById(R.id.markerInfoPanel);
        loadingScreen = findViewById(R.id.loadingScreen);
        menu = findViewById(R.id.menu);
        centerMarker = findViewById(R.id.centerMarker);

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

        // Загрузка сохраненных маркеров
        //new File(getFilesDir() + "/markers.json").delete();
        gson = new Gson();
        ArrayList<MiniMarkerInfo> markers;
        try (FileReader reader = new FileReader(getFilesDir() + "/markers.json")) {
            Type listType = new TypeToken<ArrayList<MiniMarkerInfo>>(){}.getType();
            markers = gson.fromJson(reader, listType);

        } catch (IOException e) {
            markers = new ArrayList<>();
        }
        String msg = markers.size() + " markers were downloaded from file";
        for (MiniMarkerInfo markerInfo: markers) {
            addMarker(new Point(markerInfo.getLatitude(), markerInfo.getLongitude()), markerInfo.getName(), markerInfo.getDescription(), markerInfo.getMarkerType(), markerInfo.getDateTime());
        }
        Log.d("JSON", msg);

        // Построитель маршрутов
        pedestrianRouter = TransportFactory.getInstance().createPedestrianRouter();


        // Обработчик долгого нажатия на карту
        inputListener = new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {}

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
                Toast.makeText(context, "Передвиньте маркер на нужную позицию и нажмите на него", Toast.LENGTH_LONG).show();

                mapView.getMap().move(
                        new CameraPosition(point, map.getCameraPosition().getZoom(), 0.0f, 0.0f),
                        new Animation(SMOOTH, 0.3f),
                        null
                );

                centerMarker.setVisibility(View.VISIBLE);
                centerMarker.setOnClickListener(null);
                centerMarker.setOnClickListener(v -> onCenterMarkerClick(v));
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

        // Закрытие загрузочного окна через секунду
        new Handler().postDelayed(() -> {
            loadingScreen.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
        }, LOADING_SCREEN_TIME_OUT);

        // Создание меню
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(R.drawable.back, "Закрыть меню"));
        menuItems.add(new MenuItem(R.drawable.routes, "Мои маршруты"));
        menuItems.add(new MenuItem(R.drawable.markers, "Мои метки"));
        menuItems.add(new MenuItem(R.drawable.tracks, "Мои треки"));
        menuItems.add(new MenuItem(R.drawable.settings, "Настройки"));
        menuItems.add(new MenuItem(R.drawable.exit, "Выход"));

        MenuAdapter menuAdapter = new MenuAdapter(this, menuItems);
        menu.setAdapter(menuAdapter);
        menu.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    // Действие для "Закрыть меню"
                    menu.setVisibility(View.GONE);
                    findViewById(R.id.menuButton).setVisibility(View.VISIBLE);
                    break;
                case 1:
                    // Действие для "Мои маршруты"
                    break;
                case 2:
                    // Действие для "Мои метки"
                    break;
                case 3:
                    // Действие для "Мои треки"
                    break;
                case 4:
                    // Действие для "Настройки"
                    break;
                case 5:
                    // Действие для "Выход"
                    break;
            }
        });
    }

    public static class MenuItem {
        private final int imageId;
        private final String text;

        public MenuItem(int imageId, String text) {
            this.imageId = imageId;
            this.text = text;
        }

        public int getImageId() {
            return imageId;
        }

        public String getText() {
            return text;
        }
    }

    public static class MenuAdapter extends ArrayAdapter<MenuItem> {
        private final ArrayList<MenuItem> menuItems;
        private final Context mContext;

        public MenuAdapter(Context context, ArrayList<MenuItem> items) {
            super(context, 0, items);
            mContext = context;
            menuItems = items;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null) {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.item_menu, parent, false);
            }

            MenuItem currentItem = menuItems.get(position);

            ImageView imageView = listItem.findViewById(R.id.imageView);
            imageView.setImageResource(currentItem.getImageId());

            TextView textView = listItem.findViewById(R.id.textView);
            textView.setText(currentItem.getText());

            return listItem;
        }
    }

    // Изменение положения метки при изменении текущего местоположения
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Point oldLocation = userLocation;
            userLocation = new Point(location.getLatitude(), location.getLongitude());
            locationMarker.setGeometry(userLocation);
            if (isTracking) {
                List<Point> list = new ArrayList<>();
                list.add(oldLocation);
                list.add(userLocation);
                PolylineMapObject polyline = mapObjects.addPolyline(new Polyline(list));
                trackPolylines.add(polyline);
                track.add(userLocation);
            }
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

        // Сохранение JSON в файл
        new File(getFilesDir() + "/markers.json").delete();
        ArrayList<MiniMarkerInfo> markers = new ArrayList<>();

        for (MarkerInfo markerInfo: markerInfoMap.values()) {
            double latitude = markerInfo.getPlacemark().getGeometry().getLatitude();
            double longitude = markerInfo.getPlacemark().getGeometry().getLongitude();
            markers.add(new MiniMarkerInfo(latitude, longitude, markerInfo.getName(), markerInfo.getDescription(), markerInfo.getMarkerType(), markerInfo.getDateTime()));
        }

        String json = gson.toJson(markers);
        try (FileWriter writer = new FileWriter(getFilesDir() + "/markers.json")) {
            writer.write(json);
            Log.d("JSON", markers.size() + " markers are saved");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("JSON", "Error saving markers");
        }

        MapKitFactory.getInstance().onStop();
        super.onStop();
    }


    public void onMapCenterButtonClick(View view) {
        float currentZoom = mapView.getMap().getCameraPosition().getZoom();
        mapView.getMap().move(
                new CameraPosition(userLocation, currentZoom, 0.0f, 0.0f),
                new Animation(SMOOTH, 0.5f),
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
        centerMarker.setVisibility(View.INVISIBLE);
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
            addMarker(point, markerName, markerDescription, markerType, LocalDateTime.now());
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

    public void onTrackingButtonClicked(View view) {
        Button trackingButton = findViewById(R.id.trackingButton);
        if (!isTracking) {
            trackPolylines = new ArrayList<>();
            track = new ArrayList<>();
            track.add(userLocation);
            trackingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)));
        }
        else {
            trackingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        }
        isTracking = !isTracking;
    }

    public void onRouteButtonClicked(View view) {
        Toast.makeText(getApplicationContext(), "Поставьте маркер на место, куда прокладываем маршрут", Toast.LENGTH_LONG).show();

        centerMarker.setVisibility(View.VISIBLE);
        centerMarker.setOnClickListener(null);
        centerMarker.setOnClickListener(this::onConfirmRouteMarkerClicked);
    }

    public void onConfirmRouteMarkerClicked(View v) {
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        ScreenPoint screenPoint = new ScreenPoint(mapView.getMapWindow().width() / 2f,
                mapView.getMapWindow().height() / 2f);
        Point destinationPoint = mapView.getMapWindow().screenToWorld(screenPoint);
        addMarker(destinationPoint, "", "", MarkerType.DEFAULT, LocalDateTime.now());
        requestPoints.add(new RequestPoint(userLocation, RequestPointType.WAYPOINT, null, null));
        requestPoints.add(new RequestPoint(destinationPoint, RequestPointType.WAYPOINT, null, null));
        TimeOptions timeOptions = new TimeOptions();
        Session.RouteListener routeListener = new Session.RouteListener() {
            @Override
            public void onMasstransitRoutes(@NonNull List<Route> list) {
                routePolylines = new ArrayList<>();
                PolylineMapObject polyline = mapObjects.addPolyline(list.get(0).getGeometry());
                routePolylines.add(polyline);
            }

            @Override
            public void onMasstransitRoutesError(@NonNull Error error) {
                Toast.makeText(getApplicationContext(), "Ошибка построения маршрута", Toast.LENGTH_LONG).show();
            }
        };
        pedestrianRouter.requestRoutes(requestPoints, timeOptions, routeListener);
        centerMarker.setOnClickListener(null);
        centerMarker.setVisibility(View.INVISIBLE);
    }

    private void addMarker(Point position, String name, String description, MarkerType type, LocalDateTime dateTime) {
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
        MarkerInfo markerInfo = new MarkerInfo(placemark, name, description, type, dateTime);
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

    public void onMenuButtonClick(View view) {
        menu.setVisibility(View.VISIBLE);
        findViewById(R.id.menuButton).setVisibility(View.GONE);
    }
}