package com.example.osmexample.view;

import static com.yandex.mapkit.Animation.Type.SMOOTH;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

import com.example.osmexample.model.RouteInfo;
import com.example.osmexample.presenter.Marker;
import com.example.osmexample.model.MarkerInfo;
import com.example.osmexample.model.ObjectType;
import com.example.osmexample.R;
import com.example.osmexample.presenter.Presenter;
import com.example.osmexample.presenter.Route;
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
    private LinearLayout createFormLayout = null;
    private LinearLayout markerInfoPanel = null;
    private LinearLayout startScreen = null;
    private LinearLayout createAccountLayout = null;
    private LinearLayout authorizationLayout = null;
    private LinearLayout newPasswordLayout = null;
    private ListView menu = null;
    private ListView listView = null;
    private LocationManager locationManager;
    private MapObjectCollection mapObjects = null;
    private MapObject clickedMarker = null;
    private PolylineMapObject clickedRoute = null;
    private PolylineMapObject clickedTrack = null;
    private ImageView centerMarker = null;
    private boolean isTracking = false;
    private List<Point> trackPoints = null;
    private List<PolylineMapObject> trackPolylines = null;
    private List<PolylineMapObject> routePolylines = null;
    private PolylineMapObject routePolyline = null;
    private PedestrianRouter pedestrianRouter = null;
    private TextView emptyListText = null;
    private TextView listText = null;
    private TextView createOrChangeTitleText = null;
    private TextView createOrChangeNameText = null;
    private TextView createOrChangeDescriptionText = null;
    private ArrayList<ListItem> listMarkersItems;
    private ArrayList<ListItem> listRoutesItems;
    private ArrayList<ListItem> listTracksItems;
    private ListMarkersAdapter listMarkersAdapter;
    private ListRoutesAdapter listRoutesAdapter;
    private ListTracksAdapter listTracksAdapter;
    private RadioButton mushroomRadioButton = null;
    private RadioButton fishRadioButton = null;
    private RadioButton walkRadioButton = null;
    private FrameLayout saveOrChangeRouteLayout = null;
    private Button createOrChangeButton = null;
    private Button deleteTrackOrRouteButton = null;
    private java.util.Map<ObjectType, Integer> imageViewMap = null;
    private int trackColor = Color.rgb(50, 205, 50);


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
        createFormLayout = findViewById(R.id.createFormLayout);
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
        mushroomRadioButton = findViewById(R.id.mushroomRadioButton);
        fishRadioButton = findViewById(R.id.fishRadioButton);
        walkRadioButton = findViewById(R.id.walkRadioButton);
        confirmationLayout = findViewById(R.id.confirmationLayout);
        displayLayout = findViewById(R.id.visibilityListLayout);
        saveOrChangeRouteLayout = findViewById(R.id.saveRouteLayout);
        createOrChangeButton = findViewById(R.id.createOrChangeButton);
        startScreen = findViewById(R.id.startScreen);
        createAccountLayout = findViewById(R.id.createAccountLayout);
        authorizationLayout = findViewById(R.id.authorizationLayout);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        deleteTrackOrRouteButton = findViewById(R.id.deleteTrackOrRouteButton);

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
        for (MarkerInfo markerInfo : presenter.getMarkerList()) {
            drawMarker(markerInfo);
        }
        // Загрузка сохраненных маршрутов
        for (RouteInfo routeInfo : presenter.getRouteList()) {
            drawRoute(routeInfo);
        }
        // Загрузка сохраненных треков
        for (RouteInfo trackInfo : presenter.getTrackList()) {
            drawTrack(trackInfo);
        }

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
            startScreen.setVisibility(View.VISIBLE);
        }, LOADING_SCREEN_TIME_OUT);

        imageViewMap = new HashMap<>();
        imageViewMap.put(ObjectType.DEFAULT, R.drawable.default_marker);
        imageViewMap.put(ObjectType.MUSHROOM, R.drawable.mushroom_marker);
        imageViewMap.put(ObjectType.FISH, R.drawable.fish_marker);
        imageViewMap.put(ObjectType.WALK, R.drawable.walk_marker);

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
                            listMarkersItems.add(new ListItem(imageViewMap.get(markerInfo.getMarkerType()), markerInfo.getName(), markerInfo.getDescription(), markerInfo.isVisible(), formattedDateTime, R.drawable.garbage));
                        }

                        listMarkersAdapter = new ListMarkersAdapter(this, listMarkersItems);
                        listView.setAdapter(listMarkersAdapter);
                        listView.setVisibility(View.VISIBLE);
                        emptyListText.setVisibility(View.INVISIBLE);
                    }
                    mainLayout.setVisibility(View.GONE);
                    listText.setText("Мои метки");
                    listLayout.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    // Действие для "Мои маршруты"
                    if (presenter.getRouteList().isEmpty()) {
                        listView.setVisibility(View.INVISIBLE);
                        emptyListText.setText("У вас пока нет маршрутов");
                        emptyListText.setVisibility(View.VISIBLE);
                    } else {
                        listRoutesItems = new ArrayList<>();
                        for (RouteInfo routeInfo : presenter.getRouteList()) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String formattedDateTime = routeInfo.getDateTime().format(formatter);
                            listRoutesItems.add(new ListItem(imageViewMap.get(routeInfo.getRouteType()), routeInfo.getName(), routeInfo.getDescription(), routeInfo.isVisible(), formattedDateTime, R.drawable.garbage));
                        }

                        listRoutesAdapter = new ListRoutesAdapter(this, listRoutesItems);
                        listView.setAdapter(listRoutesAdapter);
                        listView.setVisibility(View.VISIBLE);
                        emptyListText.setVisibility(View.INVISIBLE);
                    }
                    mainLayout.setVisibility(View.GONE);
                    listText.setText("Мои маршруты");
                    listLayout.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    // Действие для "Мои треки"
                    if (presenter.getTrackList().isEmpty()) {
                        listView.setVisibility(View.INVISIBLE);
                        emptyListText.setText("У вас пока нет треков");
                        emptyListText.setVisibility(View.VISIBLE);
                    } else {
                        listTracksItems = new ArrayList<>();
                        for (RouteInfo trackInfo : presenter.getTrackList()) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String formattedDateTime = trackInfo.getDateTime().format(formatter);
                            listTracksItems.add(new ListItem(imageViewMap.get(trackInfo.getRouteType()), trackInfo.getName(), trackInfo.getDescription(), trackInfo.isVisible(), formattedDateTime, R.drawable.garbage));
                        }

                        listTracksAdapter = new ListTracksAdapter(this, listTracksItems);
                        listView.setAdapter(listTracksAdapter);
                        listView.setVisibility(View.VISIBLE);
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
                polyline.setStrokeColor(trackColor);
                trackPolylines.add(polyline);
                trackPoints.add(userLocation);
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
        ImageProvider imageProvider = getImageProvider(markerInfo.getMarkerType());
        PlacemarkMapObject placemark = mapObjects.addPlacemark();
        placemark.setGeometry(markerInfo.getPoint());
        placemark.setIcon(imageProvider);
        placemark.setText(markerInfo.getName());
        placemark.setTextStyle(new TextStyle().setPlacement(TextStyle.Placement.TOP));
        placemark.setIconStyle(new IconStyle().setScale(MARKER_SCALE).setAnchor(new PointF(0.5f, 1.0f)));
        placemark.addTapListener(onMarkerTapListener);
        placemark.setVisible(markerInfo.isVisible());
        Marker marker = new Marker(placemark, markerInfo);
        presenter.addMarker(marker);
    }

    private void drawMarker(Point position, String name, String description, ObjectType type, LocalDateTime dateTime) {
        ImageProvider imageProvider = getImageProvider(type);
        PlacemarkMapObject placemark = mapObjects.addPlacemark();
        placemark.setGeometry(position);
        placemark.setIcon(imageProvider);
        placemark.setText(name);
        placemark.setTextStyle(new TextStyle().setPlacement(TextStyle.Placement.TOP));
        placemark.setIconStyle(new IconStyle().setScale(MARKER_SCALE).setAnchor(new PointF(0.5f, 1.0f)));
        placemark.addTapListener(onMarkerTapListener);
        presenter.addMarker(placemark, name, description, type, dateTime);
    }

    private void drawRoute(RouteInfo routeInfo) {
        PolylineMapObject polyline = mapObjects.addPolyline(new Polyline(routeInfo.getPoints()));
        polyline.addTapListener(routePolylineTap);
        Route route = new Route(polyline, routeInfo);
        presenter.addRoute(route);
    }

    private void drawTrack(RouteInfo trackInfo) {
        PolylineMapObject polyline = mapObjects.addPolyline(new Polyline(trackInfo.getPoints()));
        polyline.setStrokeColor(trackColor);
        polyline.addTapListener(trackPolylineTap);
        Route track = new Route(polyline, trackInfo);
        presenter.addTrack(track);
    }

    // Обработчик нажатия на метку
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

    private ImageProvider getImageProvider(ObjectType type) {
        ImageProvider imageProvider = ImageProvider.fromResource(this, R.drawable.default_marker);
        if (type == ObjectType.MUSHROOM) {
            imageProvider = ImageProvider.fromResource(this, R.drawable.mushroom_marker);
        } else if (type == ObjectType.FISH) {
            imageProvider = ImageProvider.fromResource(this, R.drawable.fish_marker);
        } else if (type == ObjectType.WALK) {
            imageProvider = ImageProvider.fromResource(this, R.drawable.walk_marker);
        }
        return imageProvider;
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

    private void openCreateFormLayout(String title, String name, String description) {
        String text_name = "Имя " + name;
        String text_descr = "Описание " + description;
        createOrChangeTitleText.setText(title);
        createOrChangeNameText.setText(text_name);
        createOrChangeDescriptionText.setText(text_descr);

        String hint_name = "Введите имя " + name;
        String hint_descr = "Введите описание " + description;
        createOrChangeInputNameText.setHint(hint_name);
        createOrChangeInputDescriptionText.setHint(hint_descr);
        createFormLayout.setVisibility(View.VISIBLE);
    }

    private void closeCreateFormLayout() {
        createOrChangeInputNameText.setText("");
        createOrChangeInputDescriptionText.setText("");
        createFormLayout.setVisibility(View.INVISIBLE);
    }

    // Обработчики для главного экрана

    // Обработчик нажатия кнопки текущего местоположения
    public void onMapCenterButtonClick(View view) {
        float currentZoom = mapView.getMap().getCameraPosition().getZoom();
        mapView.getMap().move(
                new CameraPosition(userLocation, currentZoom, 0.0f, 0.0f),
                new Animation(SMOOTH, 0.5f),
                null
        );
    }

    // Обработчик нажатия кнопки увеличения масштаба
    public void onPlusZoomButtonClick(View view) {
        Point currentPosition = mapView.getMap().getCameraPosition().getTarget();
        float currentZoom = mapView.getMap().getCameraPosition().getZoom();
        mapView.getMap().move(
                new CameraPosition(currentPosition, currentZoom + 1f, 0.0f, 0.0f),
                new Animation(SMOOTH, 0.3f),
                null
        );
    }

    // Обработчик нажатия кнопки уменьшения масштаба
    public void onMinusZoomButtonClick(View view) {
        Point currentPosition = mapView.getMap().getCameraPosition().getTarget();
        float currentZoom = mapView.getMap().getCameraPosition().getZoom();
        mapView.getMap().move(
                new CameraPosition(currentPosition, currentZoom - 1f, 0.0f, 0.0f),
                new Animation(SMOOTH, 0.3f),
                null
        );
    }

    // Обработчик кнопки "Меню" на карте
    public void onMenuButtonClick(View view) {
        menu.setVisibility(View.VISIBLE);
        findViewById(R.id.menuButton).setVisibility(View.GONE);
    }

    // Обработчик кнопки "Отображение слоев"
    public void onVisibilityListButtonClick(View view) {
        displayLayout.setVisibility(View.VISIBLE);
    }

    // Обработчик нажатия кнопки "Запись трека"
    public void onTrackingButtonClick(View view) {
        Button trackingButton = findViewById(R.id.trackingButton);
        if (!isTracking) {
            trackPolylines = new ArrayList<>();
            trackPoints = new ArrayList<>();
            trackPoints.add(userLocation);
            trackingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)));
        } else {
            trackingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            for (PolylineMapObject polyline : trackPolylines) {
                mapObjects.remove(polyline);
            }
            PolylineMapObject trackPolyline = mapObjects.addPolyline(new Polyline(trackPoints));
            trackPolyline.setStrokeColor(Color.rgb(34, 139, 34));
            trackPolyline.addTapListener(trackPolylineTap);
        }
        isTracking = !isTracking;
    }

    // Обработчик нажатия кнопки "Найти маршрут"
    public void onRouteButtonClick(View view) {
        Toast.makeText(getApplicationContext(), "Поставьте маркер на место, куда прокладываем маршрут", Toast.LENGTH_LONG).show();

        centerMarker.setVisibility(View.VISIBLE);
        centerMarker.setOnClickListener(null);
        centerMarker.setOnClickListener(this::onConfirmRouteMarkerClick);
    }

    // Обработчик нажатия на маркер при подтверждении маршрута
    public void onConfirmRouteMarkerClick(View v) {
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        ScreenPoint screenPoint = new ScreenPoint(mapView.getMapWindow().width() / 2f,
                mapView.getMapWindow().height() / 2f);
        Point destinationPoint = mapView.getMapWindow().screenToWorld(screenPoint);
        //drawMarker(destinationPoint, "", "", ObjectType.DEFAULT, LocalDateTime.now());
        requestPoints.add(new RequestPoint(userLocation, RequestPointType.WAYPOINT, null, null));
        requestPoints.add(new RequestPoint(destinationPoint, RequestPointType.WAYPOINT, null, null));
        TimeOptions timeOptions = new TimeOptions();
        Session.RouteListener routeListener = new Session.RouteListener() {
            @Override
            public void onMasstransitRoutes(@NonNull List<com.yandex.mapkit.transport.masstransit.Route> list) {
                routePolylines = new ArrayList<>();
                PolylineMapObject polyline = mapObjects.addPolyline(list.get(0).getGeometry());
                routePolylines.add(polyline);
                routePolyline = polyline;
                polyline.addTapListener(routePolylineTap);
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

    // Обработчики экрана создания/изменения объектов

    // Обработчик нажатия на центральный маркер при создании метки
    public void onCenterMarkerClick(View view) {
        findViewById(R.id.centerMarker).setVisibility(View.INVISIBLE);
        openCreateFormLayout("Создать метку", "метки", "метки");
        createOrChangeButton.setOnClickListener(this::onCreateFormCreateOrChangeMarkerButtonClick);
    }

    //Обработчик нажатия кнопки "Сохранить" или "Изменить" для меток
    public void onCreateFormCreateOrChangeMarkerButtonClick(View view) {
        ScreenPoint screenPoint = new ScreenPoint(mapView.getMapWindow().width() / 2f,
                mapView.getMapWindow().height() / 2f);
        Point point = mapView.getMapWindow().screenToWorld(screenPoint);
        String markerName = createOrChangeInputNameText.getText().toString();
        String markerDescription = createOrChangeInputDescriptionText.getText().toString();
        ObjectType markerType = ObjectType.DEFAULT;
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
        closeCreateFormLayout();
    }

    //Обработчик нажатия кнопки "Сохранить" для маршрутов
    public void onCreateFormCreateRouteButtonClick(View view) {
        String routeName = createOrChangeInputNameText.getText().toString();
        String routeDescription = createOrChangeInputDescriptionText.getText().toString();
        ObjectType routeType = ObjectType.DEFAULT;
        if (mushroomRadioButton.isChecked()) {
            routeType = ObjectType.MUSHROOM;
            mushroomRadioButton.setChecked(false);
        } else if (fishRadioButton.isChecked()) {
            routeType = ObjectType.FISH;
            fishRadioButton.setChecked(false);
        } else if (walkRadioButton.isChecked()) {
            routeType = ObjectType.WALK;
            walkRadioButton.setChecked(false);
        }
        presenter.addRoute(clickedRoute, routeName, routeDescription, routeType, LocalDateTime.now());

        closeCreateFormLayout();
    }

    //Обработчик нажатия кнопки "Изменить" для маршрутов
    public void onCreateFormChangeRouteButtonClick(View view) {
        String routeName = createOrChangeInputNameText.getText().toString();
        String routeDescription = createOrChangeInputDescriptionText.getText().toString();
        ObjectType routeType = ObjectType.DEFAULT;
        if (mushroomRadioButton.isChecked()) {
            routeType = ObjectType.MUSHROOM;
            mushroomRadioButton.setChecked(false);
        } else if (fishRadioButton.isChecked()) {
            routeType = ObjectType.FISH;
            fishRadioButton.setChecked(false);
        } else if (walkRadioButton.isChecked()) {
            routeType = ObjectType.WALK;
            walkRadioButton.setChecked(false);
        }
        Route route = presenter.getRoute(clickedRoute);
        route.setName(routeName);
        route.setDescription(routeDescription);
        route.setRouteType(routeType);

        closeCreateFormLayout();
    }

    //Обработчик нажатия кнопки "Сохранить" для треков
    public void onCreateFormCreateTrackButtonClick(View view) {
        String trackName = createOrChangeInputNameText.getText().toString();
        String trackDescription = createOrChangeInputDescriptionText.getText().toString();
        ObjectType trackType = ObjectType.DEFAULT;
        if (mushroomRadioButton.isChecked()) {
            trackType = ObjectType.MUSHROOM;
            mushroomRadioButton.setChecked(false);
        } else if (fishRadioButton.isChecked()) {
            trackType = ObjectType.FISH;
            fishRadioButton.setChecked(false);
        } else if (walkRadioButton.isChecked()) {
            trackType = ObjectType.WALK;
            walkRadioButton.setChecked(false);
        }
        presenter.addTrack(clickedTrack, trackName, trackDescription, trackType, LocalDateTime.now());

        closeCreateFormLayout();
    }

    //Обработчик нажатия кнопки "Изменить" для треков
    public void onCreateFormChangeTrackButtonClick(View view) {
        String trackName = createOrChangeInputNameText.getText().toString();
        String trackDescription = createOrChangeInputDescriptionText.getText().toString();
        ObjectType trackType = ObjectType.DEFAULT;
        if (mushroomRadioButton.isChecked()) {
            trackType = ObjectType.MUSHROOM;
            mushroomRadioButton.setChecked(false);
        } else if (fishRadioButton.isChecked()) {
            trackType = ObjectType.FISH;
            fishRadioButton.setChecked(false);
        } else if (walkRadioButton.isChecked()) {
            trackType = ObjectType.WALK;
            walkRadioButton.setChecked(false);
        }
        Route route = presenter.getTrack(clickedTrack);
        route.setName(trackName);
        route.setDescription(trackDescription);
        route.setRouteType(trackType);

        closeCreateFormLayout();
    }

    // Обработчик нажатия кнопки "Отмена" при создании/изменении объекта
    public void onCreateFormCancelButton(View view) {
        closeCreateFormLayout();
        mainLayout.setVisibility(View.VISIBLE);
        mushroomRadioButton.setChecked(false);
        fishRadioButton.setChecked(false);
        walkRadioButton.setChecked(false);
    }

    // Обработчики экрана MarkerInfo

    // Обработчик кнопки "Изменить"
    public void onMarkerInfoChangeButtonClick(View view) {
        centerMarker.setVisibility(View.INVISIBLE);
        openCreateFormLayout("Изменить метку", "метки", "метки");

        Marker marker = presenter.getMarker(clickedMarker);
        createOrChangeInputNameText.setHint(marker.getName());
        createOrChangeInputDescriptionText.setHint(marker.getDescription());

        markerInfoPanel.setVisibility(View.INVISIBLE);
    }

    public void onMarkerInfoDeleteButtonClick(View view) {
        mapObjects.remove(clickedMarker);
        presenter.removeMarker(clickedMarker);
        markerInfoPanel.setVisibility(View.INVISIBLE);
    }

    // Обработчик кнопки "Открыть список сохраненных меток"
    public void onMarkerInfoOpenListButtonClick(View view) {
        markerInfoPanel.setVisibility(View.INVISIBLE);

        // Создание списка с сохраненными метками
        if (presenter.getMarkerList().isEmpty()) {
            emptyListText.setVisibility(View.VISIBLE);
        } else {
            listMarkersItems = new ArrayList<>();
            for (MarkerInfo markerInfo : presenter.getMarkerList()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDateTime = markerInfo.getDateTime().format(formatter);
                listMarkersItems.add(new ListItem(imageViewMap.get(markerInfo.getMarkerType()), markerInfo.getName(), markerInfo.getDescription(), markerInfo.isVisible(), formattedDateTime, R.drawable.garbage));
            }

            listMarkersAdapter = new ListMarkersAdapter(this, listMarkersItems);
            listView.setAdapter(listMarkersAdapter);
            emptyListText.setVisibility(View.INVISIBLE);
        }
        mainLayout.setVisibility(View.GONE);
        listText.setText("Мои метки");

        listLayout.setVisibility(View.VISIBLE);
    }

    // Обработчик нажатия кнопки закрытия окна MarkerInfo
    public void onMarkerInfoCloseButtonClick(View view) {
        markerInfoPanel.setVisibility(View.INVISIBLE);
    }

    // Обработчики окна со списоком сохраненных меток/маршрутов/треков

    // Обработчик кнопки "Закрыть" в окне со списками сохраненных меток/маршрутов/треков
    public void onMarkerListCloseButtonClick(View view) {
        listLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.menuButton).setVisibility(View.VISIBLE);
    }

    // Обработчик кнопки "Удалить" в окне со списками сохраненных меток
    public void onMarkerListDeleteButtonClick(int position) {
        // Получаем выбранный элемент списка
        ListItem selectedItem = listMarkersItems.get(position);

        // Показываем окно с подтверждением удаления
        confirmationLayout.setVisibility(View.VISIBLE);
        TextView confirmationText = findViewById(R.id.confirmationText);
        confirmationText.setText("Вы точно хотите удалить метку?");
        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(v -> {
            listMarkersItems.remove(selectedItem);

            Marker deletedMarker = presenter.getMarker(position);

            mapObjects.remove(deletedMarker.getPlacemark());
            presenter.removeMarker(deletedMarker.getPlacemark());

            if (presenter.getMarkerList().isEmpty()) {
                emptyListText.setVisibility(View.VISIBLE);
            }

            listMarkersAdapter.notifyDataSetChanged();
            confirmationLayout.setVisibility(View.INVISIBLE);
        });
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> confirmationLayout.setVisibility(View.INVISIBLE));
    }

    // Обработчик кнопки "Отобразить" в окне со списками сохраненных меток
    public void onMarkerListSetVisibleButtonClick(int position) {
        // Получаем выбранный элемент списка
        ListItem selectedItem = listMarkersItems.get(position);

        Marker selectedMarker = presenter.getMarker(position);

        // Меняем изображение кнопки displayButton
        if (selectedMarker.isVisible()) {
            selectedItem.setImageId3(R.drawable.invisible);
            selectedMarker.getPlacemark().setVisible(false);
            selectedMarker.setVisible(false);
        } else {
            selectedItem.setImageId3(R.drawable.visible);
            selectedMarker.getPlacemark().setVisible(true);
            selectedMarker.setVisible(true);
        }
        listMarkersAdapter.notifyDataSetChanged();


    }

    public void onSaveRouteCloseButtonClick(View view) {
        saveOrChangeRouteLayout.setVisibility(View.INVISIBLE);
    }

    public void onSaveRouteDeleteButtonClick(View view) {
        mapObjects.remove(clickedRoute);
        if (presenter.getRoute(clickedRoute) != null) {
            presenter.removeRoute(clickedRoute);
        }
        saveOrChangeRouteLayout.setVisibility(View.INVISIBLE);
    }

    public void onSaveRouteDeleteTrackButtonClick(View view) {
        mapObjects.remove(clickedTrack);
        if (presenter.getTrack(clickedTrack) != null) {
            presenter.removeTrack(clickedTrack);
        }
        saveOrChangeRouteLayout.setVisibility(View.INVISIBLE);
    }

    public void onSaveRouteSaveButtonClick(View view) {
        openCreateFormLayout("Сохранить маршрут", "маршрута", "маршрута");
        saveOrChangeRouteLayout.setVisibility(View.INVISIBLE);
        createOrChangeButton.setOnClickListener(this::onCreateFormCreateRouteButtonClick);
    }

    public void onSaveRouteChangeButtonClick(View view) {
        openCreateFormLayout("Изменить маршрут", "маршрута", "маршрута");

        Route route = presenter.getRoute(clickedRoute);
        createOrChangeInputNameText.setHint(route.getName());
        createOrChangeInputDescriptionText.setHint(route.getDescription());

        saveOrChangeRouteLayout.setVisibility(View.INVISIBLE);
        createOrChangeButton.setOnClickListener(this::onCreateFormChangeRouteButtonClick);
    }

    public void onSaveRouteSaveTrackButtonClick(View view) {
        openCreateFormLayout("Сохранить трек", "трека", "трека");
        saveOrChangeRouteLayout.setVisibility(View.INVISIBLE);
        createOrChangeButton.setOnClickListener(this::onCreateFormCreateTrackButtonClick);
    }

    public void onSaveRouteChangeTrackButtonClick(View view) {
        openCreateFormLayout("Изменить трек", "трека", "трека");

        Route track = presenter.getTrack(clickedTrack);
        createOrChangeInputNameText.setText(track.getName());
        createOrChangeInputDescriptionText.setText(track.getDescription());

        saveOrChangeRouteLayout.setVisibility(View.INVISIBLE);
        createOrChangeButton.setOnClickListener(this::onCreateFormChangeTrackButtonClick);
    }

    // Обработчики окна изменения параметров отображения меток/маршрутов/треков

    // Обработчик кнопки "Закрыть" в окне отображения меток
    public void onVisibilityListCloseButtonClick(View view) {
        displayLayout.setVisibility(View.INVISIBLE);
    }

    // Обработчик нажатия на маршрут
    private final MapObjectTapListener routePolylineTap = (mapObject, point) -> {
        clickedRoute = (PolylineMapObject) mapObject;
        Button saveOrChangeButton = findViewById(R.id.doTrackOrRouteButton);
        if (presenter.getRoute(mapObject) == null) {
            saveOrChangeButton.setText("Сохранить");
            saveOrChangeButton.setOnClickListener(this::onSaveRouteSaveButtonClick);
        } else {
            saveOrChangeButton.setText("Изменить");
            saveOrChangeButton.setOnClickListener(this::onSaveRouteChangeButtonClick);
        }
        saveOrChangeRouteLayout.setVisibility(View.VISIBLE);
        deleteTrackOrRouteButton.setOnClickListener(this::onSaveRouteDeleteButtonClick);
        return false;
    };

    // Обработчик нажатия на трек
    private final MapObjectTapListener trackPolylineTap = (mapObject, point) -> {
        clickedTrack = (PolylineMapObject) mapObject;
        Button saveOrChangeButton = findViewById(R.id.doTrackOrRouteButton);
        if (presenter.getTrack(mapObject) == null) {
            saveOrChangeButton.setText("Сохранить");
            saveOrChangeButton.setOnClickListener(this::onSaveRouteSaveTrackButtonClick);
        } else {
            saveOrChangeButton.setText("Изменить");
            saveOrChangeButton.setOnClickListener(this::onSaveRouteChangeTrackButtonClick);
        }
        saveOrChangeRouteLayout.setVisibility(View.VISIBLE);
        deleteTrackOrRouteButton.setOnClickListener(this::onSaveRouteDeleteTrackButtonClick);
        return false;
    };

    public void onStartScreenRegistrationButtonClick(View view) {
        startScreen.setVisibility(View.INVISIBLE);
        createAccountLayout.setVisibility(View.VISIBLE);
    }

    public void onStartScreenLoginButtonClick(View view) {
        startScreen.setVisibility(View.INVISIBLE);
        authorizationLayout.setVisibility(View.VISIBLE);
    }

    public void onCreateAccountLayoutCreateAccountButtonClick(View view) {
        createAccountLayout.setVisibility(View.INVISIBLE);
        authorizationLayout.setVisibility(View.VISIBLE);
    }

    public void onCreateAccountLayoutHaveAccountButtonClick(View view) {
        createAccountLayout.setVisibility(View.INVISIBLE);
        authorizationLayout.setVisibility(View.VISIBLE);
    }

    public void onAuthorizationLayoutAuthorizationButtonClick(View view) {
        authorizationLayout.setVisibility(View.INVISIBLE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    public void onAuthorizationLayoutForgotPasswordButtonClick(View view) {
        TextView newPasswordText = findViewById(R.id.newPasswordText);
        newPasswordText.setText("Чтобы восстановить пароль, введите адрес вашей электронной почты");
        EditText inputEmail = findViewById(R.id.inputEmail);
        inputEmail.setVisibility(View.VISIBLE);
        Button getNewPasswordButton = findViewById(R.id.getNewPasswoButton);
        getNewPasswordButton.setText("Получить новый пароль");
        getNewPasswordButton.setOnClickListener(this::onNewPasswordLayoutGetNewPasswordButtonClick);
        authorizationLayout.setVisibility(View.INVISIBLE);
        newPasswordLayout.setVisibility(View.VISIBLE);
    }

    public void onAuthorizationLayoutCreateNewAccountButtonClick(View view) {
        authorizationLayout.setVisibility(View.INVISIBLE);
        createAccountLayout.setVisibility(View.VISIBLE);
    }

    public void onNewPasswordLayoutGetNewPasswordButtonClick(View view) {
        TextView newPasswordText = findViewById(R.id.newPasswordText);
        newPasswordText.setText("Мы отправили вам на почту новый пароль");
        EditText inputEmail = findViewById(R.id.inputEmail);
        inputEmail.setVisibility(View.INVISIBLE);
        Button returnToLoginButton = findViewById(R.id.getNewPasswoButton);
        returnToLoginButton.setText("Вернуться ко входу");
        returnToLoginButton.setOnClickListener(this::onNewPasswordLayoutReturnToLoginButtonClick);
    }

    public void onNewPasswordLayoutReturnToLoginButtonClick(View view) {
        newPasswordLayout.setVisibility(View.INVISIBLE);
        authorizationLayout.setVisibility(View.VISIBLE);
    }

    public void onRouteListDeleteButtonClick(int position) {
        // Получаем выбранный элемент списка
        ListItem selectedItem = listRoutesItems.get(position);

        // Показываем окно с подтверждением удаления
        confirmationLayout.setVisibility(View.VISIBLE);
        TextView confirmationText = findViewById(R.id.confirmationText);
        confirmationText.setText("Вы точно хотите удалить маршрут?");
        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(v -> {
            listRoutesItems.remove(selectedItem);

            //Route deletedRoute = presenter.getRoute(position);

            //mapObjects.remove(deletedRoute.getPlacemark());
            //presenter.removeMarker(deletedRoute.getPlacemark());

            if (presenter.getRouteList().isEmpty()) {
                emptyListText.setVisibility(View.VISIBLE);
            }

            listRoutesAdapter.notifyDataSetChanged();
            confirmationLayout.setVisibility(View.INVISIBLE);
        });
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> confirmationLayout.setVisibility(View.INVISIBLE));
    }

    public void onRouteListSetVisibleButtonClick(int position) {
        // Получаем выбранный элемент списка
        ListItem selectedItem = listRoutesItems.get(position);

        //Route selectedRoute = presenter.getRoute(position);

        // Меняем изображение кнопки displayButton
        /*if (selectedRoute.isVisible()) {
            selectedItem.setImageId3(R.drawable.invisible);
            selectedRoute.getPlacemark().setVisible(false);
            selectedRoute.setVisible(false);
        } else {
            selectedItem.setImageId3(R.drawable.visible);
            selectedRoute.getPlacemark().setVisible(true);
            selectedRoute.setVisible(true);
        }*/
        listRoutesAdapter.notifyDataSetChanged();
    }

    public void onTrackListDeleteButtonClick(int position) {
        // Получаем выбранный элемент списка
        ListItem selectedItem = listTracksItems.get(position);

        // Показываем окно с подтверждением удаления
        confirmationLayout.setVisibility(View.VISIBLE);
        TextView confirmationText = findViewById(R.id.confirmationText);
        confirmationText.setText("Вы точно хотите удалить трек?");
        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(v -> {
            listTracksItems.remove(selectedItem);

            //Route deletedRoute = presenter.getRoute(position);

            //mapObjects.remove(deletedRoute.getPlacemark());
            //presenter.removeMarker(deletedRoute.getPlacemark());

            if (presenter.getRouteList().isEmpty()) {
                emptyListText.setVisibility(View.VISIBLE);
            }

            listTracksAdapter.notifyDataSetChanged();
            confirmationLayout.setVisibility(View.INVISIBLE);
        });
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> confirmationLayout.setVisibility(View.INVISIBLE));
    }

    public void onTrackListSetVisibleButtonClick(int position) {
        // Получаем выбранный элемент списка
        ListItem selectedItem = listTracksItems.get(position);

        //Route selectedTrack = presenter.getTrack(position);

        // Меняем изображение кнопки displayButton
        /*if (selectedRoute.isVisible()) {
            selectedItem.setImageId3(R.drawable.invisible);
            selectedRoute.getPlacemark().setVisible(false);
            selectedRoute.setVisible(false);
        } else {
            selectedItem.setImageId3(R.drawable.visible);
            selectedRoute.getPlacemark().setVisible(true);
            selectedRoute.setVisible(true);
        }*/
        listTracksAdapter.notifyDataSetChanged();
    }
}