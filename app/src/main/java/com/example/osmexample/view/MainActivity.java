package com.example.osmexample.view;

import static com.yandex.mapkit.Animation.Type.SMOOTH;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osmexample.presenter.Marker;
import com.example.osmexample.model.MarkerInfo;
import com.example.osmexample.model.ObjectType;
import com.example.osmexample.R;
import com.example.osmexample.presenter.Presenter;
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
import com.yandex.mapkit.transport.masstransit.Session;
import com.yandex.mapkit.transport.masstransit.TimeOptions;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String API_KEY = "1c1210f8-c152-4c8d-96ae-ac504e3662c4";
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private final int LOADING_SCREEN_TIME_OUT = 1000;
    private final float MARKER_SCALE = 0.06f;
    private final float LOCATION_MARKER_SCALE = 0.14f;
    private final float START_ZOOM = 14.0f;
    private Presenter presenter = null;
    private MapView mapView = null;
    private Point userLocation = new Point(0, 0);
    private PlacemarkMapObject locationMarker = null;
    private EditText createOrChangeInputNameText = null;
    private EditText createOrChangeInputDescriptionText = null;
    private FrameLayout loadingScreen = null;
    private FrameLayout mainLayout = null;
    private FrameLayout listLayout = null;
    private FrameLayout confirmationLayout = null;
    private FrameLayout displayLayout = null;
    private LinearLayout setMarkerLayout = null;
    private LinearLayout markerInfoPanel = null;
    private ListView menu = null;
    private ListView listView = null;
    private LocationManager locationManager;
    private MapObjectCollection mapObjects = null;
    private MapObject clickedMarker = null;
    private ImageView centerMarker = null;
    private boolean isTracking = false;
    private List<Point> track = null;
    private List<PolylineMapObject> trackPolylines = null;
    private List<PolylineMapObject> routePolylines = null;
    private PedestrianRouter pedestrianRouter = null;
    private TextView emptyListText = null;
    private TextView listText = null;
    private TextView createOrChangeTitleText = null;
    private TextView createOrChangeNameText = null;
    private TextView createOrChangeDescriptionText = null;
    private ArrayList<ListItem> listMarkersItems;
    private ListItemsAdapter listMarkersAdapter;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(API_KEY);
        MapKitFactory.initialize(this);

        setContentView(R.layout.activity_main);

        // Инициализация компонентов модели и представителя
        presenter = new Presenter(getFilesDir().getAbsolutePath());

        // Инициализация компонентов карты
        mapView = findViewById(R.id.mapview);
        mainLayout = findViewById(R.id.MainLayout);
        setMarkerLayout = findViewById(R.id.createOrChangeLayout);
        markerInfoPanel = findViewById(R.id.markerInfoPanel);
        loadingScreen = findViewById(R.id.loadingScreen);
        menu = findViewById(R.id.menu);
        centerMarker = findViewById(R.id.centerMarker);
        emptyListText = findViewById(R.id.emptyListText);
        listLayout = findViewById(R.id.listLayout);
        listView = findViewById(R.id.listView);
        listText = findViewById(R.id.listText);
        createOrChangeTitleText = findViewById(R.id.createOrChangeTitleText);
        createOrChangeNameText = findViewById(R.id.createOrChangeNameText);
        createOrChangeInputNameText = findViewById(R.id.createOrChangeInputName);
        createOrChangeDescriptionText = findViewById(R.id.createOrChangeDescriptionText);
        createOrChangeInputDescriptionText = findViewById(R.id.createOrChangeInputDescription);
        confirmationLayout = findViewById(R.id.confirmationLayout);
        displayLayout = findViewById(R.id.displayLayout);

        // Определение местоположения
        requestLocationPermission();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastLocation != null) {
            userLocation = new Point(lastLocation.getLatitude(), lastLocation.getLongitude());
        }

        // Добавление маркера текущего местоположения
        addCurrentLocationMarker();

        // Загрузка сохраненных маркеров
        List<MarkerInfo> markerInfos = presenter.getMarkerList();
        for (MarkerInfo markerInfo : markerInfos) {
            drawMarker(markerInfo);
        }
//        // Загрузка сохраненных маршрутов
//        List<MiniRouteInfo> routes = FileManager.loadRoutesFromFile(getFilesDir().getAbsolutePath());
//        for (MiniRouteInfo routeInfo : routes) {
//            addRoute(new Point(routeInfo.getLatitude(), routeInfo.getLongitude()), routeInfo.getName(), routeInfo.getDescription(), routeInfo.getMarkerType(), routeInfo.getDateTime());
//        }
//        // Загрузка сохраненных треков
//        List<MiniRouteInfo> tracks = FileManager.loadTracksFromFile(getFilesDir().getAbsolutePath());
//        for (MiniRouteInfo trackInfo : tracks) {
//            addTrack(new Point(trackInfo.getLatitude(), trackInfo.getLongitude()), trackInfo.getName(), trackInfo.getDescription(), trackInfo.getMarkerType(), trackInfo.getDateTime());
//        }

        // Построитель маршрутов
        pedestrianRouter = TransportFactory.getInstance().createPedestrianRouter();


        // Обработчик долгого нажатия на карту

        mapView.getMap().addInputListener(longTapListener);
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
        createMenu();
    }

    private final InputListener longTapListener = new InputListener() {
        @Override
        public void onMapTap(@NonNull Map map, @NonNull Point point) {
        }

        @Override
        public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
            Toast.makeText(getApplicationContext(),
                    "Передвиньте маркер на нужную позицию и нажмите на него",
                    Toast.LENGTH_LONG).show();

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

    private void createMenu() {
        // Создание выпадающего меню
        ArrayList<com.example.osmexample.view.MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new com.example.osmexample.view.MenuItem(R.drawable.back, "Закрыть меню"));
        menuItems.add(new com.example.osmexample.view.MenuItem(R.drawable.markers, "Мои метки"));
        menuItems.add(new com.example.osmexample.view.MenuItem(R.drawable.routes, "Мои маршруты"));
        menuItems.add(new com.example.osmexample.view.MenuItem(R.drawable.tracks, "Мои треки"));
        menuItems.add(new com.example.osmexample.view.MenuItem(R.drawable.exit, "Выход"));
        // Для вывода меню на экран
        com.example.osmexample.view.MenuAdapter menuAdapter = new com.example.osmexample.view.MenuAdapter(this, menuItems);
        menu.setAdapter(menuAdapter);
        // Создание мапы: тип метки - ссылка на изображение
        java.util.Map<ObjectType, Integer> imageViewMap = new HashMap<>();
        imageViewMap.put(ObjectType.DEFAULT, R.drawable.default_marker);
        imageViewMap.put(ObjectType.MUSHROOM, R.drawable.mushroom_marker);
        imageViewMap.put(ObjectType.FISH, R.drawable.fish_marker);
        imageViewMap.put(ObjectType.WALK, R.drawable.walk_marker);

        menu.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    // Действие для "Закрыть меню"
                    menu.setVisibility(View.GONE);
                    findViewById(R.id.menuButton).setVisibility(View.VISIBLE);
                    break;
                case 1:
                    // Действие для "Мои метки"
                    // Создание списка с сохраненными метками
                    if (presenter.getMarkerList().isEmpty()) {
                        emptyListText.setVisibility(View.VISIBLE);
                    } else {
                        listMarkersItems = new ArrayList<>();
                        for (MarkerInfo markerInfo : presenter.getMarkerList()) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String formattedDateTime = markerInfo.getDateTime().format(formatter);
                            listMarkersItems.add(new ListItem(imageViewMap.get(markerInfo.getMarkerType()), markerInfo.getName(), markerInfo.getDescription(), formattedDateTime, R.drawable.garbage, R.drawable.visible));
                        }

                        listMarkersAdapter = new ListItemsAdapter(this, listMarkersItems);
                        listView.setAdapter(listMarkersAdapter);
                        emptyListText.setVisibility(View.INVISIBLE);
                    }
                    mainLayout.setVisibility(View.GONE);
                    listText.setText("Мои метки");
                    listLayout.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    // Действие для "Мои маршруты"
                    if (presenter.getMarkerList().isEmpty()) {
                        listView.setVisibility(View.INVISIBLE);
                        emptyListText.setText("У вас пока нет маршрутов");
                        emptyListText.setVisibility(View.VISIBLE);
                    } else {
                        ArrayList<ListItem> listRoutesItems = new ArrayList<>();
                        for (MarkerInfo markerInfo : presenter.getMarkerList()) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String formattedDateTime = markerInfo.getDateTime().format(formatter);
                            listRoutesItems.add(new ListItem(imageViewMap.get(markerInfo.getMarkerType()), markerInfo.getName(), markerInfo.getDescription(), formattedDateTime, R.drawable.garbage, R.drawable.visible));
                        }

                        ListItemsAdapter listRoutesAdapter = new ListItemsAdapter(this, listRoutesItems);
                        listView.setAdapter(listRoutesAdapter);
                        emptyListText.setVisibility(View.INVISIBLE);
                    }
                    mainLayout.setVisibility(View.GONE);
                    listText.setText("Мои маршруты");
                    listLayout.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    // Действие для "Мои треки"
                    if (presenter.getMarkerList().isEmpty()) {
                        listView.setVisibility(View.INVISIBLE);
                        emptyListText.setText("У вас пока нет треков");
                        emptyListText.setVisibility(View.VISIBLE);
                    } else {
                        ArrayList<ListItem> listTracksItems = new ArrayList<>();
                        for (MarkerInfo markerInfo : presenter.getMarkerList()) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String formattedDateTime = markerInfo.getDateTime().format(formatter);
                            listTracksItems.add(new ListItem(imageViewMap.get(markerInfo.getMarkerType()), markerInfo.getName(), markerInfo.getDescription(), formattedDateTime, R.drawable.garbage, R.drawable.visible));
                        }

                        ListItemsAdapter listTracksAdapter = new ListItemsAdapter(this, listTracksItems);
                        listView.setAdapter(listTracksAdapter);
                        emptyListText.setVisibility(View.INVISIBLE);
                    }
                    mainLayout.setVisibility(View.GONE);
                    listText.setText("Мои треки");
                    listLayout.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    // Действие для "Выход"
                    break;
            }
        });
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
        public void onProviderDisabled(@NonNull String provider) {
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
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
        presenter.saveData();

        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    private void addCurrentLocationMarker() {
        mapObjects = mapView.getMap().getMapObjects().addCollection();
        ImageProvider imageProvider = ImageProvider.fromResource(this, R.drawable.star);
        locationMarker = mapObjects.addPlacemark();
        locationMarker.setGeometry(userLocation);
        locationMarker.setIcon(imageProvider);
        locationMarker.setIconStyle(new IconStyle().setScale(LOCATION_MARKER_SCALE));
        locationMarker.addTapListener(onMarkerTapListener);
    }

    private void drawMarker(MarkerInfo markerInfo) {
        ImageProvider imageProvider = ImageProvider.fromResource(this, R.drawable.default_marker);
        if (markerInfo.getMarkerType() == ObjectType.MUSHROOM) {
            imageProvider = ImageProvider.fromResource(this, R.drawable.mushroom_marker);
        } else if (markerInfo.getMarkerType() == ObjectType.FISH) {
            imageProvider = ImageProvider.fromResource(this, R.drawable.fish_marker);
        } else if (markerInfo.getMarkerType() == ObjectType.WALK) {
            imageProvider = ImageProvider.fromResource(this, R.drawable.walk_marker);
        }
        PlacemarkMapObject placemark = mapObjects.addPlacemark();
        placemark.setGeometry(markerInfo.getPoint());
        placemark.setIcon(imageProvider);
        placemark.setText(markerInfo.getName());
        placemark.setTextStyle(new TextStyle().setPlacement(TextStyle.Placement.TOP));
        placemark.setIconStyle(new IconStyle().setScale(MARKER_SCALE).setAnchor(new PointF(0.5f, 1.0f)));
        placemark.addTapListener(onMarkerTapListener);
        Marker marker = new Marker(placemark, markerInfo);
        presenter.addMarker(marker);
    }

    private void drawMarker(Point position, String name, String description, ObjectType type, LocalDateTime dateTime) {
        ImageProvider imageProvider = ImageProvider.fromResource(this, R.drawable.default_marker);
        if (type == ObjectType.MUSHROOM) {
            imageProvider = ImageProvider.fromResource(this, R.drawable.mushroom_marker);
        } else if (type == ObjectType.FISH) {
            imageProvider = ImageProvider.fromResource(this, R.drawable.fish_marker);
        } else if (type == ObjectType.WALK) {
            imageProvider = ImageProvider.fromResource(this, R.drawable.walk_marker);
        }
        PlacemarkMapObject placemark = mapObjects.addPlacemark();
        placemark.setGeometry(position);
        placemark.setIcon(imageProvider);
        placemark.setText(name);
        placemark.setTextStyle(new TextStyle().setPlacement(TextStyle.Placement.TOP));
        placemark.setIconStyle(new IconStyle().setScale(MARKER_SCALE).setAnchor(new PointF(0.5f, 1.0f)));
        placemark.addTapListener(onMarkerTapListener);
        presenter.addMarker(placemark, name, description, type, dateTime);
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

    public void onCenterMarkerClick(View view) {;
        findViewById(R.id.centerMarker).setVisibility(View.INVISIBLE);
        createOrChangeTitleText.setText("Создать метку");
        createOrChangeNameText.setText("Имя метки");
        createOrChangeInputNameText.setHint("Введите имя метки");
        createOrChangeDescriptionText.setText("Описание метки");
        createOrChangeInputDescriptionText.setHint("Введите описание метки");
        // Тут надо установить слушателя на кнопку buttonCreateOrChange(buttonSave) для создания метки
        setMarkerLayout.setVisibility(View.VISIBLE);
    }

    public void onCreateOrChangeButton(View view) {
        ScreenPoint screenPoint = new ScreenPoint(mapView.getMapWindow().width() / 2f,
                mapView.getMapWindow().height() / 2f);
        Point point = mapView.getMapWindow().screenToWorld(screenPoint);
        TextView nameView = findViewById(R.id.createOrChangeInputName);
        String markerName = nameView.getText().toString();
        TextView descriptionView = findViewById(R.id.createOrChangeInputDescription);
        String markerDescription = descriptionView.getText().toString();
        ObjectType markerType = ObjectType.DEFAULT;
        RadioButton mushroomRadioButton = findViewById(R.id.mushroomRadioButton);
        RadioButton fishRadioButton = findViewById(R.id.fishRadioButton);
        RadioButton walkRadioButton = findViewById(R.id.walkRadioButton);
        ImageProvider imageProvider = ImageProvider.fromResource(this, R.drawable.default_marker);
        if (mushroomRadioButton.isChecked()) {
            markerType = ObjectType.MUSHROOM;
            imageProvider = ImageProvider.fromResource(this, R.drawable.mushroom_marker);
            mushroomRadioButton.setChecked(false);
        } else if (fishRadioButton.isChecked()) {
            markerType = ObjectType.FISH;
            imageProvider = ImageProvider.fromResource(this, R.drawable.fish_marker);
            fishRadioButton.setChecked(false);
        } else if (walkRadioButton.isChecked()) {
            markerType = ObjectType.WALK;
            imageProvider = ImageProvider.fromResource(this, R.drawable.walk_marker);
            walkRadioButton.setChecked(false);
        }
        if (markerInfoPanel.getVisibility() == View.VISIBLE) {
            Marker marker = presenter.getMarker(clickedMarker);
            if (marker != null) {
                marker.setName(markerName);
                marker.setDescription(markerDescription);
                marker.setMarkerType(markerType);

                PlacemarkMapObject placemark = marker.getPlacemark();
                placemark.setText(markerName);

                placemark.setIcon(imageProvider);
                placemark.setTextStyle(new TextStyle().setPlacement(TextStyle.Placement.TOP));
                placemark.setIconStyle(new IconStyle().setScale(MARKER_SCALE).setAnchor(new PointF(0.5f, 1.0f)));

                TextView textName = findViewById(R.id.markerName);
                textName.setText(marker.getName());
                TextView textDescription = findViewById(R.id.markerDescription);
                textDescription.setText(marker.getDescription());
            }
        } else {
            drawMarker(point, markerName, markerDescription, markerType, LocalDateTime.now());
        }
        nameView.setText("");
        descriptionView.setText("");
        setMarkerLayout.setVisibility(View.INVISIBLE);
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
        findViewById(R.id.centerMarker).setVisibility(View.INVISIBLE);
        createOrChangeTitleText.setText("Изменить метку");
        createOrChangeNameText.setText("Имя метки");
        createOrChangeDescriptionText.setText("Описание метки");

        Marker marker = presenter.getMarker(clickedMarker);
        if (marker == null) {
            createOrChangeInputNameText.setHint("Введите имя метки");
            createOrChangeInputDescriptionText.setHint("Введите описание метки");
        } else {
            createOrChangeInputNameText.setHint(marker.getName());
            createOrChangeInputDescriptionText.setHint(marker.getDescription());
        }
        markerInfoPanel.setVisibility(View.INVISIBLE);
        setMarkerLayout.setVisibility(View.VISIBLE);
    }

    public void onDeleteMarkerButtonClicked(View view) {
        mapObjects.remove(clickedMarker);
        presenter.removeMarker(clickedMarker);
        markerInfoPanel.setVisibility(View.INVISIBLE);
    }

    public void onTrackingButtonClicked(View view) {
        Button trackingButton = findViewById(R.id.trackingButton);
        if (!isTracking) {
            trackPolylines = new ArrayList<>();
            track = new ArrayList<>();
            track.add(userLocation);
            trackingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)));
        } else {
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
        drawMarker(destinationPoint, "", "", ObjectType.DEFAULT, LocalDateTime.now());
        requestPoints.add(new RequestPoint(userLocation, RequestPointType.WAYPOINT, null, null));
        requestPoints.add(new RequestPoint(destinationPoint, RequestPointType.WAYPOINT, null, null));
        TimeOptions timeOptions = new TimeOptions();
        Session.RouteListener routeListener = new Session.RouteListener() {
            @Override
            public void onMasstransitRoutes(@NonNull List<com.yandex.mapkit.transport.masstransit.Route> list) {
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
            Marker marker = presenter.getMarker(mapObject);
            if (marker == null) {
                Toast.makeText(getApplicationContext(), "Маркер не найден", Toast.LENGTH_LONG).show();
            } else {
                TextView markerName = findViewById(R.id.markerName);
                markerName.setText(marker.getName());
                TextView markerDescription = findViewById(R.id.markerDescription);
                markerDescription.setText(marker.getDescription());
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

    // Обработчик кнопки "Меню" на карте
    public void onMenuButtonClick(View view) {
        menu.setVisibility(View.VISIBLE);
        findViewById(R.id.menuButton).setVisibility(View.GONE);
    }

    // Обработчик кнопки "Закрыть" в окне со списками сохраненных меток/маршрутов/треков
    public void onListCloseButtonClick(View view) {
        listLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.menuButton).setVisibility(View.VISIBLE);
    }

    // Обработчик кнопки "Открыть список сохраненных меток" в окне MarkerInfoPanel
    public void onOpenListMarkersButtonClicked(View view) {
        markerInfoPanel.setVisibility(View.INVISIBLE);
        listLayout.setVisibility(View.VISIBLE);
    }

    // Обработчик кнопки "Отображение меток"
    public void onDisplayButtonClick(View view) {
        displayLayout.setVisibility(View.VISIBLE);
    }

    // Обработчик кнопки "Закрыть" в окне отображения меток
    public void onDisplayLayoutButtonClick(View view) {
        displayLayout.setVisibility(View.INVISIBLE);
    }

    // Обработчик кнопки "Удалить" в окне со списками сохраненных меток
    public void onDeleteButtonClick(int position) {
        // Получаем выбранный элемент списка
        ListItem selectedItem = listMarkersItems.get(position);

        // Показываем окно с подтверждением удаления
        confirmationLayout.setVisibility(View.VISIBLE);
        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(v -> {
            listMarkersItems.remove(selectedItem);
            // Надо удалить метку из карты и добавить проверку на пустой список, если список пустой
            // emptyListText.setVisibility(View.VISIBLE);
            listMarkersAdapter.notifyDataSetChanged();
            confirmationLayout.setVisibility(View.INVISIBLE);
        });
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> confirmationLayout.setVisibility(View.INVISIBLE));
    }

    // Обработчик кнопки "Отобразить" в окне со списками сохраненных меток
    public void onDisplayButtonClick(int position) {
        // Получаем выбранный элемент списка
        ListItem selectedItem = listMarkersItems.get(position);

        // Меняем изображение кнопки displayButton
        if (selectedItem.getImageId3() == R.drawable.visible) {
            selectedItem.setImageId3(R.drawable.invisible);
        } else {
            selectedItem.setImageId3(R.drawable.visible);
        }
        listMarkersAdapter.notifyDataSetChanged();
    }
}